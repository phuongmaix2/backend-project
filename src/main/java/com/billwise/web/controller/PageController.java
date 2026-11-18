package com.billwise.web.controller;

import com.billwise.service.GroupService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Serves public and authenticated Thymeleaf pages. */
@Controller
public class PageController {

    private final GroupService groupService;

    public PageController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        String username = authentication.getName();

        model.addAttribute("username", username);

        model.addAttribute(
                "groups",
                groupService.findGroupsForUser(username));

        return "dashboard";
    }

}
