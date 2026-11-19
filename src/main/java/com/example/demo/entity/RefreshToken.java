package com.example.demo.entity;

import java.time.Instant;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
            @Index(name = "idx_refresh_tokens_family_id", columnList = "family_id"),
            @Index(name = "idx_refresh_tokens_cleanup", columnList = "expiresAt, revokedAt")
        })
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false)
public class RefreshToken extends BaseEntity {
    @Column(unique = true, nullable = false, columnDefinition = "CHAR(64)")
    private String token;

    @Column(nullable = false, length = 64)
    private String familyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private Instant expiresAt;

    private Instant revokedAt;
}
