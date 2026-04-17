package com.khubeev.service;

import com.khubeev.config.properties.MailProperties;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailProperties mailProperties;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    private void setupMailProperties() {
        when(mailProperties.from()).thenReturn("test@example.com");
        when(mailProperties.sender()).thenReturn("Test Sender");
        when(mailProperties.subject()).thenReturn("Test Subject");
        when(mailProperties.content()).thenReturn("Hello $name, click $url");
        when(mailProperties.baseUrl()).thenReturn("http://localhost:8080");
    }

    @Test
    void sendVerificationEmail_ShouldSendEmail() {
        setupMailProperties();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail("user@example.com", "testuser", "verification-code");

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmail_WhenMessagingException_ShouldThrowRuntimeException() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail error"));

        assertThatThrownBy(() -> emailService.sendVerificationEmail("user@example.com", "testuser", "code"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mail error");
    }
}