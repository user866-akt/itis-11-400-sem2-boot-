package com.khubeev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.khubeev.config.TestSecurityConfig;
import com.khubeev.dto.CreateUserRequest;
import com.khubeev.dto.UpdateUserRequest;
import com.khubeev.dto.UserDto;
import com.khubeev.service.JpaUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JpaUserController.class)
@Import(TestSecurityConfig.class)
class JpaUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JpaUserService jpaUserService;

    private ObjectMapper objectMapper;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testUserDto = new UserDto(1L, "testuser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_ShouldReturnUsers() throws Exception {
        when(jpaUserService.findAll()).thenReturn(List.of(testUserDto));

        mockMvc.perform(get("/jpa/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_WhenUserExists_ShouldReturnUser() throws Exception {
        when(jpaUserService.findById(1L)).thenReturn(testUserDto);

        mockMvc.perform(get("/jpa/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(jpaUserService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/jpa/users/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByUsername_WhenUserExists_ShouldReturnUser() throws Exception {
        when(jpaUserService.findByUsername("testuser")).thenReturn(testUserDto);

        mockMvc.perform(get("/jpa/users/search")
                        .param("username", "testuser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        when(jpaUserService.createUser(eq("newuser"), eq("password123"), eq("new@example.com")))
                .thenReturn(testUserDto);

        mockMvc.perform(post("/jpa/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setEmail("");

        when(jpaUserService.createUser(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/jpa/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_ShouldUpdateAndReturnUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updateduser");

        when(jpaUserService.updateUser(eq(1L), eq("updateduser")))
                .thenReturn(testUserDto);

        mockMvc.perform(put("/jpa/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(jpaUserService).deleteUser(1L);

        mockMvc.perform(delete("/jpa/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_ShouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        when(jpaUserService.createUser(any(), any(), any()))
                .thenReturn(testUserDto);

        mockMvc.perform(post("/jpa/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByUsername_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(jpaUserService.findByUsername("unknown")).thenReturn(null);

        mockMvc.perform(get("/jpa/users/search")
                        .param("username", "unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updateduser");

        when(jpaUserService.updateUser(eq(99L), any()))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/jpa/users/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_WithInvalidData_ShouldReturnBadRequestWithError() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setEmail("");

        when(jpaUserService.createUser(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Username can't be empty!"));

        mockMvc.perform(post("/jpa/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username can't be empty!"));
    }
}