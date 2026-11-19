package com.example.demo.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.config.JwtProperties;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class JwksController {

    private final JWKSource<SecurityContext> jwkSource;
    private final JwtProperties jwtProperties;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() throws KeySourceException {
        // Select all keys from JWKSource and strip private key material automatically
        JWKSelector selector = new JWKSelector(new JWKMatcher.Builder().build());
        JWKSet jwkSet = new JWKSet(jwkSource.get(selector, null));

        // toJSONObject() defaults to publicKeysOnly = true (never exposes private keys)
        return jwkSet.toJSONObject();
    }

    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> openidConfiguration() {
        String baseUrl =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return Map.of("issuer", jwtProperties.issuer(), "jwks_uri", baseUrl + "/.well-known/jwks.json");
    }
}
