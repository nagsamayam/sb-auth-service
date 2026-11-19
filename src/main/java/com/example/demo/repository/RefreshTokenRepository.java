package com.example.demo.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RefreshToken;

import lombok.NonNull;

@Repository
public interface RefreshTokenRepository extends JpaRepository<@NonNull RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken rf SET rf.revokedAt = :revokedAt WHERE rf.familyId = :familyId")
    void revokeAllByFamilyId(@Param("revokedAt") Instant revokedAt, @Param("familyId") String familyId);

    @Modifying
    @Query(
            "DELETE FROM RefreshToken rf WHERE rf.expiresAt < :now OR (rf.revokedAt IS NOT NULL AND rf.revokedAt < :revokedCutoff)")
    int deleteExpiredAndRevokedTokens(@Param("now") Instant now, @Param("revokedCutoff") Instant revokedCutoff);
}
