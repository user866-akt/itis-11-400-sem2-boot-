package com.khubeev.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NoteTest {

    @Test
    void note_DefaultConstructor_ShouldInitializeCreatedAt() {
        Note note = new Note();
        assertThat(note.getCreatedAt()).isNotNull();
    }

    @Test
    void note_ConstructorWithParameters_ShouldSetValues() {
        User author = new User();
        author.setId(1L);

        Note note = new Note("Test Title", "Test Content", true, author);

        assertThat(note.getTitle()).isEqualTo("Test Title");
        assertThat(note.getContent()).isEqualTo("Test Content");
        assertThat(note.isPublic()).isTrue();
        assertThat(note.getAuthor()).isEqualTo(author);
        assertThat(note.getCreatedAt()).isNotNull();
    }

    @Test
    void note_ShouldSetAndGetValues() {
        Note note = new Note();
        User author = new User();
        LocalDateTime now = LocalDateTime.now();

        note.setId(1L);
        note.setTitle("Test Title");
        note.setContent("Test Content");
        note.setCreatedAt(now);
        note.setPublic(true);
        note.setAuthor(author);

        assertThat(note.getId()).isEqualTo(1L);
        assertThat(note.getTitle()).isEqualTo("Test Title");
        assertThat(note.getContent()).isEqualTo("Test Content");
        assertThat(note.getCreatedAt()).isEqualTo(now);
        assertThat(note.isPublic()).isTrue();
        assertThat(note.getAuthor()).isEqualTo(author);
    }
}