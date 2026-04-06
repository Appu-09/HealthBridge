package com.healthbridge.healthbridge.controller;

import com.healthbridge.healthbridge.service.TrendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trends")
@RequiredArgsConstructor
@Tag(name = "Trend Analysis", description = "AI powered health trend monitoring across multiple reports")
@SecurityRequirement(name = "Bearer Authentication")
public class TrendController {

    private final TrendService trendService;

    @Operation(summary = "Analyze health trends for a family member",
            description = "Compares last 3 reports and identifies improving, worsening, and stable values. Flags urgent concerns.")
    @GetMapping("/analyze/{memberId}")
    public ResponseEntity<String> analyzeTrends(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(
                trendService.analyzeTrendsForMember(
                        userDetails.getUsername(), memberId));
    }
}