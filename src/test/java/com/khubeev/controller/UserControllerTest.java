package com.khubeev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khubeev.config.TestSecurityConfig;
import com.khubeev.dto.CreateUserRequest;
import com.khubeev.dto.UpdateUserRequest;
import com.khubeev.dto.UserDto;
import com.khubeev.service.HibernateUserService;
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

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HibernateUserService hibernateUserService;

    private ObjectMapper objectMapper;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testUserDto = new UserDto(1L, "testuser");
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnUsers() throws Exception {
        when(hibernateUserService.findAll()).thenReturn(List.of(testUserDto));

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser
    void findById_WhenUserExists_ShouldReturnUser() throws Exception {
        when(hibernateUserService.findById(1L)).thenReturn(testUserDto);

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void findById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(hibernateUserService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/users/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");

        when(hibernateUserService.createUser(eq("newuser"))).thenReturn(testUserDto);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("");

        when(hibernateUserService.createUser(any()))
                .thenThrow(new IllegalArgumentException("Username can't be empty!"));

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateUser_ShouldUpdateAndReturnUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updateduser");

        when(hibernateUserService.updateUser(eq(1L), eq("updateduser")))
                .thenReturn(testUserDto);

        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void updateUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updateduser");

        when(hibernateUserService.updateUser(eq(99L), any()))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/users/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(hibernateUserService).deleteUser(1L);

        mockMvc.perform(delete("/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}