/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.api.dto.response;

import lombok.*;

/**
 * AuthenticationResponse
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AuthenticationResponse {
    private String token;
}