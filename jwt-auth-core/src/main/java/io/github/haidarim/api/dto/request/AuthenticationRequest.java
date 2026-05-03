/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */
package io.github.haidarim.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AuthenticationRequest Dto
 *
 * <p>Represents authentication request data including username,
 *  email, unique identifier, password, and token.</p>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthenticationRequest {
    private String username;
    private String email;
    private String uniqueNumber;
    private String password;
    private String token;
    private String issuer;
    private String audience;
}