package com.example.demo.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtProperties;
import com.example.demo.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenService refreshTokenService;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String userId;
        if (authentication.getPrincipal() instanceof User user) {
            userId = user.getUsername();
        } else {
            userId = authentication.getName();
        }

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(jwtProperties.activeKey().keyId())
                .build();

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(jwtProperties.issuer())
                .audience(jwtProperties.audiences())
                .issuedAt(now)
                .subject(userId)
                .expiresAt(now.plusSeconds(jwtProperties.expiry()))
                .claim("authorities", authorities)
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));

        return jwt.getTokenValue();
    }

    public String generateRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        RefreshTokenService.RefreshTokenResponse refreshToken = refreshTokenService.createRefreshToken(user, null);

        return refreshToken.rawToken();
    }
}
