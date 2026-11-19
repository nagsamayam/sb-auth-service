package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.cookie")
public record CookieProperties(RefreshTokenCookie refreshToken) {
    public record RefreshTokenCookie(
            String name, String path, boolean secure, boolean httpOnly, String sameSite, Long maxAgeSeconds) {}
}
