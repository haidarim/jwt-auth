/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.api;

/**
 * JwtAuthProperties
 */
public interface JwtAuthProperties {
    String AUTHORIZATION_HEADER = "Authorization";
    String BEARER_PREFIX = "Bearer ";
    String[] WHITE_LIST = {"/api/v0/auth/register", "/api/v0/auth/authenticate"};
}

