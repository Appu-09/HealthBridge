package com.healthbridge.healthbridge.controller;

import com.healthbridge.healthbridge.dto.request.FamilyMemberRequest;
import com.healthbridge.healthbridge.dto.response.FamilyMemberResponse;
import com.healthbridge.healthbridge.service.FamilyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
@Tag(name = "Family Members", description = "Manage family members under one account")
@SecurityRequirement(name = "Bearer Authentication")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    @Operation(summary = "Add a family member",
            description = "Add father, mother, spouse or any family member to track their health")
    @PostMapping("/add")
    public ResponseEntity<FamilyMemberResponse> addMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FamilyMemberRequest request) {
        return ResponseEntity.ok(
                familyMemberService.addMember(userDetails.getUsername(), request));
    }

    @Operation(summary = "Get all family members")
    @GetMapping("/all")
    public ResponseEntity<List<FamilyMemberResponse>> getAllMembers(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                familyMemberService.getAllMembers(userDetails.getUsername()));
    }

    @Operation(summary = "Get a specific family member by ID")
    @GetMapping("/{memberId}")
    public ResponseEntity<FamilyMemberResponse> getMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(
                familyMemberService.getMember(userDetails.getUsername(), memberId));
    }

    @Operation(summary = "Delete a family member")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long memberId) {
        familyMemberService.deleteMember(userDetails.getUsername(), memberId);
        return ResponseEntity.ok("Family member deleted successfully");
    }
}