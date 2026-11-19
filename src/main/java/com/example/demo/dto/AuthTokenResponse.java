package com.example.demo.dto;

import lombok.Builder;

@Builder
public record AuthTokenResponse(
        String accessToken, String tokenType, Long expiresIn, String refreshToken, Long refreshTokenExpiresIn) {}
