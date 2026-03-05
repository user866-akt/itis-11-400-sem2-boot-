package com.khubeev.controller;

import com.khubeev.model.User;
import com.khubeev.repository.UserRepository;
import com.khubeev.service.HelloService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

    private final HelloService helloService;
    private final UserRepository userRepository;

    public HelloController(HelloService helloService, UserRepository userRepository) {
        this.helloService = helloService;
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(required = false, name = "name") String name) {
        return helloService.sayHello(name);
    }
}