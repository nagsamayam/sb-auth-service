package com.example.demo.dto;

import java.time.Instant;
import java.util.List;

import lombok.Builder;

@Builder
public record UserResponse(Long id, String username, List<String> roles, Instant createdAt) {}
