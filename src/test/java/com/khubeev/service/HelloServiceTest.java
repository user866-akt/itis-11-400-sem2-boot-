package com.khubeev.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HelloServiceTest {

    @InjectMocks
    private HelloService helloService;

    @Test
    void sayHello_WithName_ShouldReturnGreeting() {
        String result = helloService.sayHello("John");
        assertThat(result).isEqualTo("Hello, John");
    }

    @Test
    void sayHello_WithNullName_ShouldReturnHelloNull() {
        String result = helloService.sayHello(null);
        assertThat(result).isEqualTo("Hello, null");
    }
}