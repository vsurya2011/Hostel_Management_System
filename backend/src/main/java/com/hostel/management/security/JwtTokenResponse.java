package com.hostel.management.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtTokenResponse {
    @Builder.Default
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;

    // When true, no usable tokens are issued (account is not enabled yet) —
    // e.g. a warden registration awaiting admin approval. The frontend should
    // not treat this as a logged-in session.
    @Builder.Default
    private boolean pendingApproval = false;
    private String message;
}
