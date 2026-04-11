package com.khubeev.controller;

import com.khubeev.config.TestSecurityConfig;
import com.khubeev.service.JpaUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VerificationController.class)
@Import(TestSecurityConfig.class)
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JpaUserService jpaUserService;

    @Test
    @WithMockUser
    void verifyAccount_WithValidCode_ShouldRedirectWithSuccess() throws Exception {
        doNothing().when(jpaUserService).verifyUser("valid-code");

        mockMvc.perform(get("/verification").param("code", "valid-code"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser
    void verifyAccount_WithInvalidCode_ShouldRedirectWithError() throws Exception {
        doThrow(new RuntimeException("Invalid verification code!"))
                .when(jpaUserService).verifyUser("invalid");

        mockMvc.perform(get("/verification").param("code", "invalid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("error"));
    }
}