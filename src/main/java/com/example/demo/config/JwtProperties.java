package com.example.demo.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.security.jwt")
public record JwtProperties(String issuer, Long expiry, Long refreshExpiry, List<String> audiences, Set<KeySpec> keys) {
    public record KeySpec(String keyId, RSAPublicKey publicKey, RSAPrivateKey privateKey, boolean active) {}

    public KeySpec activeKey() {
        return keys.stream()
                .filter(KeySpec::active)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active JWT signing key configured"));
    }
}
