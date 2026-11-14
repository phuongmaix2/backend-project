package com.billwise.web.controller;

import com.billwise.service.GroupService;
import com.billwise.web.dto.group.GroupResponse;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.billwise.web.dto.group.GroupRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupRestController {

    private final GroupService groupService;

    public GroupRestController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupResponse> findGroups(
            Authentication authentication) {

        return groupService.findGroupsForUser(
                authentication.getName());
    }

    @GetMapping("/{groupId}")
    public GroupResponse findGroup(
            @PathVariable Long groupId,
            Authentication authentication) {

        return groupService.findGroupForUser(
                groupId,
                authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(
            @Valid @RequestBody GroupRequest request,
            Authentication authentication) {

        return groupService.createGroup(
                authentication.getName(),
                request);
    }

    @PutMapping("/{groupId}")
    public GroupResponse updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupRequest request,
            Authentication authentication) {

        return groupService.updateGroup(
                groupId,
                authentication.getName(),
                request);
    }
}