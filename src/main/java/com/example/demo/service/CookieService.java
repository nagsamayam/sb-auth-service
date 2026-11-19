package com.example.demo.service;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.example.demo.config.CookieProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieProperties cookieProperties;

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        CookieProperties.RefreshTokenCookie refreshTokenCookie = cookieProperties.refreshToken();
        return ResponseCookie.from(refreshTokenCookie.name(), refreshToken)
                .httpOnly(refreshTokenCookie.httpOnly())
                .secure(refreshTokenCookie.secure())
                .sameSite(refreshTokenCookie.sameSite())
                .maxAge(Duration.ofSeconds(refreshTokenCookie.maxAgeSeconds()))
                .path(refreshTokenCookie.path())
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        CookieProperties.RefreshTokenCookie refreshTokenCookie = cookieProperties.refreshToken();
        return ResponseCookie.from(refreshTokenCookie.name(), "")
                .httpOnly(refreshTokenCookie.httpOnly())
                .secure(refreshTokenCookie.secure())
                .sameSite(refreshTokenCookie.sameSite())
                .maxAge(Duration.ZERO)
                .path(refreshTokenCookie.path())
                .build();
    }
}
