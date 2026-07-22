package com.hostel.management.controller;

import com.hostel.management.dto.request.LoginRequest;
import com.hostel.management.dto.request.RefreshTokenRequest;
import com.hostel.management.dto.request.RegisterRequest;
import com.hostel.management.security.JwtTokenResponse;
import com.hostel.management.service.AuthService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        JwtTokenResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authService.refreshToken(request)));
    }
}
