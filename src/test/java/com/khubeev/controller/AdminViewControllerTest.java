package com.khubeev.controller;

import com.khubeev.config.TestSecurityConfig;
import com.khubeev.dto.NoteDto;
import com.khubeev.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminViewController.class)
@Import(TestSecurityConfig.class)
class AdminViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    private List<NoteDto> testNotes;

    @BeforeEach
    void setUp() {
        testNotes = List.of(
                new NoteDto(1L, "Note 1", "Content 1", LocalDateTime.now(), true, "user1"),
                new NoteDto(2L, "Note 2", "Content 2", LocalDateTime.now(), false, "user2")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllNotes_ShouldReturnAdminNotesView() throws Exception {
        when(noteService.findAllNotesForAdmin()).thenReturn(testNotes);

        mockMvc.perform(get("/admin/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_notes"))
                .andExpect(model().attributeExists("notes"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteNote_ShouldDeleteAndRedirect() throws Exception {
        when(noteService.deleteNoteByAdmin(1L)).thenReturn(testNotes.get(0));

        mockMvc.perform(post("/admin/notes/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/notes"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteNote_WhenNotFound_ShouldRedirectWithError() throws Exception {
        doThrow(new RuntimeException("Note not found")).when(noteService).deleteNoteByAdmin(99L);

        mockMvc.perform(post("/admin/notes/99/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/notes"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoints_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/notes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllNotes_WhenNoNotes_ShouldReturnEmptyList() throws Exception {
        when(noteService.findAllNotesForAdmin()).thenReturn(List.of());

        mockMvc.perform(get("/admin/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", List.of()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteNote_WhenServiceThrowsRuntimeException_ShouldHandleGracefully() throws Exception {
        doThrow(new RuntimeException("Database error")).when(noteService).deleteNoteByAdmin(1L);

        mockMvc.perform(post("/admin/notes/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/notes"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Database error"));
    }
}