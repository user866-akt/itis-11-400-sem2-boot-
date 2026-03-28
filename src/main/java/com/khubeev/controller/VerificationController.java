package com.khubeev.controller;

import com.khubeev.service.JpaUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VerificationController {

    private final JpaUserService jpaUserService;

    public VerificationController(JpaUserService jpaUserService) {
        this.jpaUserService = jpaUserService;
    }

    @GetMapping("/verification")
    public String verifyAccount(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        try {
            jpaUserService.verifyUser(code);
            redirectAttributes.addFlashAttribute("success", "Your account has been verified successfully! You can now login.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/login";
    }
}