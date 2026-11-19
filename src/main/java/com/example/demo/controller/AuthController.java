package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.*;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.CookieService;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<@NonNull UserRegistrationResponseDto> createUser(
            @RequestBody UserRegisterRequestDto request) {
        UserRegistrationResponseDto responseDto = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<@NonNull AuthTokenResponse> login(@RequestBody UserLoginRequestDto request) {
        AuthTokenResponse responseDto = authService.login(request);
        ResponseCookie cookie = cookieService.createRefreshTokenCookie(responseDto.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<@NonNull AuthTokenResponse> refreshToken(
            Authentication authentication,
            @CookieValue(name = "${app.security.cookie.refresh-token.name}", required = false) String refreshToken) {
        ResponseCookie cookie = cookieService.createRefreshTokenCookie(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authService.refreshToken(authentication, refreshToken));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<@NonNull UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.findByUsername(jwt.getSubject());

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream().map(Role::getName).toList(),
                user.getCreatedAt()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<@NonNull Void> logout(
            @CookieValue(name = "${app.security.cookie.refresh-token.name}", required = false) String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        ResponseCookie cookie = cookieService.clearRefreshTokenCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
