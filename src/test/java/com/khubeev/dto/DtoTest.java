package com.khubeev.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void createUserRequest_ShouldSetAndGetValues() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("test@example.com");

        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getPassword()).isEqualTo("password");
        assertThat(request.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void updateUserRequest_ShouldSetAndGetValues() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updateduser");

        assertThat(request.getUsername()).isEqualTo("updateduser");
    }

    @Test
    void userDto_ShouldSetAndGetValues() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getUsername()).isEqualTo("testuser");
    }

    @Test
    void userDto_Constructor_ShouldSetValues() {
        UserDto userDto = new UserDto(1L, "testuser");

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getUsername()).isEqualTo("testuser");
    }

    @Test
    void noteDto_ShouldSetAndGetValues() {
        NoteDto noteDto = new NoteDto();
        LocalDateTime now = LocalDateTime.now();

        noteDto.setId(1L);
        noteDto.setTitle("Test Title");
        noteDto.setContent("Test Content");
        noteDto.setCreatedAt(now);
        noteDto.setPublic(true);
        noteDto.setAuthorUsername("testuser");

        assertThat(noteDto.getId()).isEqualTo(1L);
        assertThat(noteDto.getTitle()).isEqualTo("Test Title");
        assertThat(noteDto.getContent()).isEqualTo("Test Content");
        assertThat(noteDto.getCreatedAt()).isEqualTo(now);
        assertThat(noteDto.isPublic()).isTrue();
        assertThat(noteDto.getAuthorUsername()).isEqualTo("testuser");
    }

    @Test
    void noteDto_Constructor_ShouldSetValues() {
        LocalDateTime now = LocalDateTime.now();
        NoteDto noteDto = new NoteDto(1L, "Test Title", "Test Content", now, true, "testuser");

        assertThat(noteDto.getId()).isEqualTo(1L);
        assertThat(noteDto.getTitle()).isEqualTo("Test Title");
        assertThat(noteDto.getContent()).isEqualTo("Test Content");
        assertThat(noteDto.getCreatedAt()).isEqualTo(now);
        assertThat(noteDto.isPublic()).isTrue();
        assertThat(noteDto.getAuthorUsername()).isEqualTo("testuser");
    }

    @Test
    void noteDto_getCreatedAtAsDate_ShouldConvertToDate() {
        LocalDateTime now = LocalDateTime.now();
        NoteDto noteDto = new NoteDto();
        noteDto.setCreatedAt(now);

        assertThat(noteDto.getCreatedAtAsDate()).isNotNull();
    }

    @Test
    void noteDto_getCreatedAtAsDate_WhenCreatedAtNull_ShouldReturnNull() {
        NoteDto noteDto = new NoteDto();
        noteDto.setCreatedAt(null);

        assertThat(noteDto.getCreatedAtAsDate()).isNull();
    }

    @Test
    void createNoteRequest_ShouldSetAndGetValues() {
        CreateNoteRequest request = new CreateNoteRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setPublic(true);

        assertThat(request.getTitle()).isEqualTo("Test Title");
        assertThat(request.getContent()).isEqualTo("Test Content");
        assertThat(request.isPublic()).isTrue();
    }

    @Test
    void updateNoteRequest_ShouldSetAndGetValues() {
        UpdateNoteRequest request = new UpdateNoteRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");
        request.setPublic(false);

        assertThat(request.getTitle()).isEqualTo("Updated Title");
        assertThat(request.getContent()).isEqualTo("Updated Content");
        assertThat(request.isPublic()).isFalse();
    }
}