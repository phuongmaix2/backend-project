package com.billwise.web.controller;

import com.billwise.service.ExpenseService;
import com.billwise.web.dto.expense.ExpenseRequest;
import com.billwise.web.dto.expense.ExpenseResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
/** REST endpoints for personal and group expense operations. */
public class ExpenseRestController {

    private final ExpenseService expenseService;

    public ExpenseRestController(
            ExpenseService expenseService) {

        this.expenseService = expenseService;
    }

    @GetMapping("/expenses/personal")
    public List<ExpenseResponse> findPersonalExpenses(
            Authentication authentication) {

        return expenseService.findPersonalExpenses(
                authentication.getName());
    }

    @GetMapping("/groups/{groupId}/expenses")
    public List<ExpenseResponse> findGroupExpenses(
            @PathVariable Long groupId,
            Authentication authentication) {

        return expenseService.findGroupExpenses(
                groupId,
                authentication.getName());
    }

    @PostMapping("/groups/{groupId}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createGroupExpense(
            @PathVariable Long groupId,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        request.setGroupId(groupId);

        return expenseService.createExpense(
                authentication.getName(),
                request);
    }

    @PostMapping("/expenses/personal")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createPersonalExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        request.setGroupId(null);

        return expenseService.createExpense(
                authentication.getName(),
                request);
    }
}
