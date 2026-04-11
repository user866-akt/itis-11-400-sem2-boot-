package com.khubeev.service;

import com.khubeev.dto.NoteDto;
import com.khubeev.model.Note;
import com.khubeev.model.User;
import com.khubeev.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private User testUser;
    private User otherUser;
    private Note testNote;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");

        testNote = new Note();
        testNote.setId(1L);
        testNote.setTitle("Test Title");
        testNote.setContent("Test Content");
        testNote.setPublic(false);
        testNote.setAuthor(testUser);
        testNote.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findAllByUser_ShouldReturnUserNotes() {
        when(noteRepository.findByAuthorOrderByCreatedAtDesc(testUser))
                .thenReturn(List.of(testNote));

        List<NoteDto> result = noteService.findAllByUser(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Title");
        verify(noteRepository).findByAuthorOrderByCreatedAtDesc(testUser);
    }

    @Test
    void findAllByUser_WhenUserIsNull_ShouldReturnEmptyList() {
        List<NoteDto> result = noteService.findAllByUser(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findAllPublicNotes_ShouldReturnPublicNotes() {
        when(noteRepository.findAllPublicNotesOrderByCreatedAtDesc())
                .thenReturn(List.of(testNote));

        List<NoteDto> result = noteService.findAllPublicNotes();

        assertThat(result).hasSize(1);
        verify(noteRepository).findAllPublicNotesOrderByCreatedAtDesc();
    }

    @Test
    void findAllNotesForAdmin_ShouldReturnAllNotes() {
        when(noteRepository.findAll()).thenReturn(List.of(testNote));

        List<NoteDto> result = noteService.findAllNotesForAdmin();

        assertThat(result).hasSize(1);
        verify(noteRepository).findAll();
    }

    @Test
    void createNote_ShouldCreateAndReturnNote() {
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        NoteDto result = noteService.createNote(testUser, "New Title", "New Content", true);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Title");
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void createNote_WithEmptyTitle_ShouldThrowException() {
        assertThatThrownBy(() -> noteService.createNote(testUser, "", "Content", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be empty");
    }

    @Test
    void createNote_WithEmptyContent_ShouldThrowException() {
        assertThatThrownBy(() -> noteService.createNote(testUser, "Title", "", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content cannot be empty");
    }

    @Test
    void createNote_WithNullUser_ShouldThrowException() {
        assertThatThrownBy(() -> noteService.createNote(null, "Title", "Content", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User cannot be null");
    }

    @Test
    void updateNote_ShouldUpdateAndReturnNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        NoteDto result = noteService.updateNote(1L, testUser, "Updated Title", "Updated Content", true);

        assertThat(result).isNotNull();
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void updateNote_WhenNoteNotFound_ShouldThrowException() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.updateNote(99L, testUser, "Title", "Content", false))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Note not found with id: 99");
    }

    @Test
    void updateNote_WhenUserIsNotOwner_ShouldThrowAccessDeniedException() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        assertThatThrownBy(() -> noteService.updateNote(1L, otherUser, "Title", "Content", false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You don't have permission to modify this note");
    }

    @Test
    void deleteNote_ShouldDeleteNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        doNothing().when(noteRepository).delete(any(Note.class));

        noteService.deleteNote(1L, testUser);

        verify(noteRepository).delete(testNote);
    }

    @Test
    void deleteNote_WhenUserIsNotOwner_ShouldThrowAccessDeniedException() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        assertThatThrownBy(() -> noteService.deleteNote(1L, otherUser))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteNoteByAdmin_ShouldDeleteAndReturnNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        doNothing().when(noteRepository).delete(any(Note.class));

        NoteDto result = noteService.deleteNoteByAdmin(1L);

        assertThat(result).isNotNull();
        verify(noteRepository).delete(testNote);
    }

    @Test
    void findNoteByIdForEdit_ShouldReturnNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        NoteDto result = noteService.findNoteByIdForEdit(1L, testUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
}