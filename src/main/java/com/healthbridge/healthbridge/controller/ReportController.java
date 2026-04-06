package com.healthbridge.healthbridge.controller;

import com.healthbridge.healthbridge.dto.response.ReportResponse;
import com.healthbridge.healthbridge.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Health Reports", description = "Upload PDF reports and get AI explanations")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Upload a blood test PDF report",
            description = "Upload a PDF from Dr Lal, Thyrocare, Apollo etc. AI will explain it in your preferred language with Indian diet recommendations.")
    @PostMapping("/upload/{memberId}")
    public ResponseEntity<ReportResponse> uploadReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long memberId,
            @RequestParam String labName,
            @RequestParam String reportType,
            @RequestParam MultipartFile file) {
        return ResponseEntity.ok(reportService.uploadReport(
                userDetails.getUsername(), memberId, labName, reportType, file));
    }

    @Operation(summary = "Get all reports for a family member")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ReportResponse>> getReports(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(
                reportService.getReports(userDetails.getUsername(), memberId));
    }

    @Operation(summary = "Get a specific report")
    @GetMapping("/member/{memberId}/report/{reportId}")
    public ResponseEntity<ReportResponse> getReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long memberId,
            @PathVariable Long reportId) {
        return ResponseEntity.ok(
                reportService.getReport(userDetails.getUsername(), memberId, reportId));
    }
}