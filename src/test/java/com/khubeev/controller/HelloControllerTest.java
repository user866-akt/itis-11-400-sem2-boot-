package com.khubeev.controller;

import com.khubeev.config.TestSecurityConfig;
import com.khubeev.repository.HibernateUserRepository;
import com.khubeev.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
@Import(TestSecurityConfig.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelloService helloService;

    @MockitoBean
    private HibernateUserRepository hibernateUserRepository;

    @Test
    @WithMockUser
    void hello_WithName_ShouldReturnGreeting() throws Exception {
        when(helloService.sayHello("John")).thenReturn("Hello, John");

        mockMvc.perform(get("/hello").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, John"));
    }

    @Test
    @WithMockUser
    void hello_WithoutName_ShouldReturnHelloNull() throws Exception {
        when(helloService.sayHello(null)).thenReturn("Hello, null");

        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, null"));
    }
}