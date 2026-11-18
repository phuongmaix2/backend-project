package com.billwise.web.controller;

import com.billwise.exception.ConflictException;
import com.billwise.service.UserRegistrationService;
import com.billwise.web.dto.auth.RegistrationForm;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/** Handles the browser registration form and redirects after successful signup. */
@Controller
public class RegistrationController {

    private final UserRegistrationService registrationService;

    public RegistrationController(
        UserRegistrationService registrationService
    ) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute(
            "registrationForm",
            new RegistrationForm()
        );

        return "register";
    }

    @PostMapping("/register")
    public String register(
        @Valid
        @ModelAttribute("registrationForm")
        RegistrationForm form,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            registrationService.register(
                form.toRegisterRequest()
            );
        } catch (ConflictException exception) {
            model.addAttribute(
                "registrationError",
                exception.getMessage()
            );

            return "register";
        }

        return "redirect:/login?registered";
    }
}
