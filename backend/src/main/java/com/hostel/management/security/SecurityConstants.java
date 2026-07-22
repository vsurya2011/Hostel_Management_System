package com.hostel.management.security;

public class SecurityConstants {
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/actuator/health"
    };

    private SecurityConstants() {
    }
}
