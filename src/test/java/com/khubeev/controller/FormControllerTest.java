package com.khubeev.controller;

import com.khubeev.config.TestSecurityConfig;
import com.khubeev.dto.UserDto;
import com.khubeev.service.JpaUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormController.class)
@Import(TestSecurityConfig.class)
class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JpaUserService jpaUserService;

    @Test
    @WithMockUser
    void registerForm_ShouldCreateUserAndRedirect() throws Exception {
        UserDto userDto = new UserDto(1L, "newuser");
        when(jpaUserService.createUser(eq("newuser"), eq("password123"), eq("email@test.com")))
                .thenReturn(userDto);

        mockMvc.perform(post("/forms/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("email", "email@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser
    void registerForm_WithInvalidData_ShouldRedirectBackWithError() throws Exception {
        doThrow(new IllegalArgumentException("Username already exists!"))
                .when(jpaUserService).createUser(any(), any(), any());

        mockMvc.perform(post("/forms/register")
                        .with(csrf())
                        .param("username", "existing")
                        .param("password", "pass")
                        .param("email", "email@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("error"));
    }
}