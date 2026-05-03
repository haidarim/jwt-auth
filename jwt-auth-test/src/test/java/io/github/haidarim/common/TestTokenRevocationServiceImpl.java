/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.common;


import io.github.haidarim.api.service.TokenRevocationService;
import io.github.haidarim.entity.RevokedToken;
import io.github.haidarim.repository.RevokedTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@RequiredArgsConstructor
public class TestTokenRevocationServiceImpl implements TokenRevocationService {
    private final Logger LOGGER = LoggerFactory.getLogger(TestTokenRevocationServiceImpl.class);
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    public void revokeToken(String jti, long expiresAt) {
        RevokedToken revokedToken = RevokedToken.builder()
                .jti(jti)
                .expiresAt(new Date(expiresAt))
                .build();

        revokedTokenRepository.save(revokedToken);
    }

    @Override
    public boolean isTokenRevoked(String jti) {
        return revokedTokenRepository.existsByJti(jti);
    }

    @Transactional
    @Override
    public int deleteExpiredTokens() {
        int affectedRows = revokedTokenRepository.deleteExpiredTokens();
        LOGGER.info("Deleted {} expired revoked tokens", affectedRows);
        return affectedRows;
    }
}
