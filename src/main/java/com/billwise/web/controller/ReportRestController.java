package com.billwise.web.controller;

import com.billwise.service.ReportService;
import com.billwise.web.dto.report.SpendingReportItem;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class ReportRestController {

    private final ReportService reportService;

    public ReportRestController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{groupId}/reports/spending")
    public List<SpendingReportItem> spendingByCategory(
            @PathVariable Long groupId,
            Authentication authentication) {

        return reportService.spendingByCategory(
                groupId,
                authentication.getName()
        );
    }
}
