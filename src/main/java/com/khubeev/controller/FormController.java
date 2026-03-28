package com.khubeev.controller;

import com.khubeev.dto.CreateUserRequest;
import com.khubeev.service.JpaUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/forms")
public class FormController {

    private final JpaUserService jpaUserService;

    public FormController(JpaUserService jpaUserService) {
        this.jpaUserService = jpaUserService;
    }

    @PostMapping("/register")
    public String registerForm(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("email") String email,
                               RedirectAttributes redirectAttributes) {
        try {
            jpaUserService.createUser(username, password, email);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please check your email to verify your account.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}