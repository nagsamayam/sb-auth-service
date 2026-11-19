package com.example.demo.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.config.JwtProperties;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.utility.RefreshTokenGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public record RefreshTokenResponse(RefreshToken refreshToken, String rawToken) {}

    @Transactional
    public RefreshTokenResponse createRefreshToken(User user, String familyId) {

        String rawToken = RefreshTokenGenerator.generateRawToken();
        String hashedToken = RefreshTokenGenerator.hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(hashedToken);
        refreshToken.setFamilyId(familyId != null ? familyId : UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(jwtProperties.refreshExpiry()));
        refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResponse(refreshToken, rawToken);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public RefreshTokenResponse rotateRefreshToken(String rawToken) {
        RefreshToken existing = refreshTokenRepository
                .findByToken(RefreshTokenGenerator.hashToken(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        // Reuse detected: Revoke the entire token family and commit the revocation
        if (existing.getRevokedAt() != null) {
            refreshTokenRepository.revokeAllByFamilyId(Instant.now(), existing.getFamilyId());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token reuse detected. Session revoked.");
        }

        // Check Expiration (Normal Session Timeout)
        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        // Invalidate current token and issue its successor in the same family
        existing.setRevokedAt(Instant.now());
        refreshTokenRepository.save(existing);

        return createRefreshToken(existing.getUser(), existing.getFamilyId());
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void revokeRefreshToken(String rawToken) {
        RefreshToken existing = refreshTokenRepository
                .findByToken(RefreshTokenGenerator.hashToken(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        refreshTokenRepository.revokeAllByFamilyId(Instant.now(), existing.getFamilyId());
    }
}
