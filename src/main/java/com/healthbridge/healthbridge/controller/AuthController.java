package com.healthbridge.healthbridge.controller;

import com.healthbridge.healthbridge.dto.request.LoginRequest;
import com.healthbridge.healthbridge.dto.request.RegisterRequest;
import com.healthbridge.healthbridge.dto.response.AuthResponse;
import com.healthbridge.healthbridge.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login APIs")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user",
            description = "Creates a new user account. Choose preferred language: ENGLISH, HINDI, or TELUGU")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login",
            description = "Login with email and password. Returns a JWT token to use in all other APIs.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}