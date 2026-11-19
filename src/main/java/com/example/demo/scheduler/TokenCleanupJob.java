package com.example.demo.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${app.jobs.refresh-token-cleanup.cron}")
    @SchedulerLock(
            name = "purgeExpiredRefreshTokens",
            lockAtMostFor = "${app.jobs.refresh-token-cleanup.lock-at-most-for}",
            lockAtLeastFor = "${app.jobs.refresh-token-cleanup.lock-at-least-for}")
    @Transactional
    public void purgeExpiredRefreshTokens() {
        LockAssert.assertLocked();
        log.info("Purging expired refresh tokens");
        int deleteCount = refreshTokenRepository.deleteExpiredAndRevokedTokens(
                Instant.now(), Instant.now().minus(24, ChronoUnit.HOURS));
        log.info("Deleted {} expired refresh tokens", deleteCount);
    }
}
