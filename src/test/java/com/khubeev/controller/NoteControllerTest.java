package com.khubeev.controller;

import com.khubeev.config.TestSecurityConfig;
import com.khubeev.dto.NoteDto;
import com.khubeev.model.User;
import com.khubeev.service.NoteService;
import com.khubeev.utils.WithCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@Import(TestSecurityConfig.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    private NoteDto testNoteDto;

    @BeforeEach
    void setUp() {
        testNoteDto = new NoteDto(
                1L,
                "Test Note",
                "Test Content",
                LocalDateTime.now(),
                false,
                "testuser"
        );
    }

    @Test
    @WithCustomUser(username = "testuser", role = "USER")
    void myNotes_ShouldReturnNotesView() throws Exception {
        when(noteService.findAllByUser(any(User.class))).thenReturn(List.of(testNoteDto));

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"));
    }

    @Test
    @Disabled
    void publicNotes_ShouldReturnPublicNotesView() throws Exception {
        when(noteService.findAllPublicNotes()).thenReturn(List.of(testNoteDto));

        mockMvc.perform(get("/notes/public"))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attributeExists("notes"));
    }

    @Test
    @WithCustomUser(username = "testuser", role = "USER")
    void showCreateForm_ShouldReturnCreateFormView() throws Exception {
        mockMvc.perform(get("/notes/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @WithCustomUser(username = "testuser", role = "USER")
    void createNote_ShouldCreateAndRedirect() throws Exception {
        when(noteService.createNote(any(User.class), eq("New Title"), eq("New Content"), eq(true)))
                .thenReturn(testNoteDto);

        mockMvc.perform(post("/notes/create")
                        .with(csrf())
                        .param("title", "New Title")
                        .param("content", "New Content")
                        .param("isPublic", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithCustomUser(username = "testuser", role = "USER")
    void createNote_WithInvalidData_ShouldRedirectBack() throws Exception {
        when(noteService.createNote(any(User.class), eq(""), any(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Title cannot be empty"));

        mockMvc.perform(post("/notes/create")
                        .with(csrf())
                        .param("title", "")
                        .param("content", "Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes/create"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithCustomUser(username = "testuser", role = "USER")
    void showEditForm_ShouldReturnEditForm() throws Exception {
        when(noteService.findNoteByIdForEdit(eq(1L), any(User.class))).thenReturn(testNoteDto);

        mockMvc.perform(get("/notes/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @WithCustomUser(username = "testuser", role = "USER")
    void updateNote_ShouldUpdateAndRedirect() throws Exception {
        when(noteService.updateNote(eq(1L), any(User.class), eq("Updated"), eq("Content"), eq(false)))
                .thenReturn(testNoteDto);

        mockMvc.perform(post("/notes/1/edit")
                        .with(csrf())
                        .param("title", "Updated")
                        .param("content", "Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithCustomUser(username = "testuser", role = "USER")
    void deleteNote_ShouldDeleteAndRedirect() throws Exception {
        doNothing().when(noteService).deleteNote(eq(1L), any(User.class));

        mockMvc.perform(post("/notes/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"))
                .andExpect(flash().attributeExists("success"));
    }
}