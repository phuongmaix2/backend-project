package com.billwise.web.controller;

import com.billwise.service.GroupService;
import com.billwise.web.dto.group.GroupRequest;
import com.billwise.web.dto.group.AddGroupMemberRequest;

import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GroupPageController {

    private final GroupService groupService;

    public GroupPageController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/groups")
    public String showGroups() {
        return "redirect:/dashboard";
    }

    @GetMapping("/groups/new")
    public String showCreateGroupForm(Model model) {
        GroupRequest request = new GroupRequest();

        // EUR is shown as the default currency in the form.
        request.setBaseCurrency("EUR");

        model.addAttribute("groupRequest", request);

        return "groups/form";
    }

    @PostMapping("/groups")
    public String createGroup(
            @Valid @ModelAttribute("groupRequest") GroupRequest request,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return "groups/form";
        }

        groupService.createGroup(
                authentication.getName(),
                request);

        return "redirect:/groups";
    }

    @GetMapping("/groups/{groupId}")
    public String showGroup(
            @PathVariable Long groupId,
            Authentication authentication,
            Model model) {

        String currentUsername = authentication.getName();

        model.addAttribute(
                "group",
                groupService.findGroupForUser(
                        groupId,
                        currentUsername));

        model.addAttribute(
                "members",
                groupService.findMembers(
                        groupId,
                        currentUsername));

        model.addAttribute(
                "currentUsername",
                currentUsername);

        model.addAttribute(
                "addGroupMemberRequest",
                new AddGroupMemberRequest());

        return "groups/detail";
    }

    @PostMapping("/groups/{groupId}/members")
    public String addMember(
            @PathVariable Long groupId,
            @Valid @ModelAttribute("addGroupMemberRequest") AddGroupMemberRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {

        String currentUsername = authentication.getName();

        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "group",
                    groupService.findGroupForUser(
                            groupId,
                            currentUsername));

            model.addAttribute(
                    "members",
                    groupService.findMembers(
                            groupId,
                            currentUsername));

            model.addAttribute(
                    "currentUsername",
                    currentUsername);

            return "groups/detail";
        }

        groupService.addMember(
                groupId,
                currentUsername,
                request);

        return "redirect:/groups/" + groupId;
    }

    @PostMapping("/groups/{groupId}/members/{userId}/remove")
    public String removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            Authentication authentication) {

        groupService.removeMember(
                groupId,
                userId,
                authentication.getName());

        return "redirect:/groups/" + groupId;
    }
}