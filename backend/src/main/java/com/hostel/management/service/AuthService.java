package com.hostel.management.service;

import com.hostel.management.dto.request.LoginRequest;
import com.hostel.management.dto.request.RefreshTokenRequest;
import com.hostel.management.dto.request.RegisterRequest;
import com.hostel.management.security.JwtTokenResponse;

public interface AuthService {
    JwtTokenResponse login(LoginRequest request);
    JwtTokenResponse register(RegisterRequest request);
    JwtTokenResponse refreshToken(RefreshTokenRequest request);
}
