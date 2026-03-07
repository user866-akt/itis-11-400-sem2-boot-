package com.khubeev.controller;

import com.khubeev.repository.HibernateUserRepository;
import com.khubeev.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final HelloService helloService;
    private final HibernateUserRepository hibernateUserRepository;

    public HelloController(HelloService helloService, HibernateUserRepository hibernateUserRepository) {
        this.helloService = helloService;
        this.hibernateUserRepository = hibernateUserRepository;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(required = false, name = "name") String name) {
        return helloService.sayHello(name);
    }
}