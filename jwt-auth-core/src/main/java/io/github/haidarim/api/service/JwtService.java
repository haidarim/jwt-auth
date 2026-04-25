/*
 * Copyright (c) 2026 haidarim
 * All rights reserved.
 *
 * This software is provided for personal, non-commercial use only.
 *
 * Unauthorized copying, modification, redistribution, or use in
 * commercial products or services is strictly prohibited.
 *
 * You may fork and modify this code solely for the purpose of
 * contributing bug fixes or improvements back to the original
 * repository via pull requests.
 *
 * All other uses require explicit written permission from the author.
 */

package io.github.haidarim.api.service;

import io.jsonwebtoken.Claims;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.function.Function;


/**
 * JwtService
 */
public interface JwtService {

    /**
     * To create jwt token
     * @param username String
     * @return token String
     * @throws NoSuchAlgorithmException if wrong algorithm is given for token creation
     * @throws InvalidKeySpecException if wrong key is given for token creation
     */
    String createToken(String username) throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Get subject associated with the specified token
     * @param token String
     * @return subject String
     * @throws NoSuchAlgorithmException if algorithm is invalid
     * @throws InvalidKeySpecException if key is invalid
     */
    String getSubject(String token)
            throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Get claim for given token using specified claim resolver
     * @param token String
     * @param claimResolver Function
     * @return T Generic
     * @param <T> T
     * @throws NoSuchAlgorithmException if algorithm is invalid
     * @throws InvalidKeySpecException if key is invalid
     */
    <T> T getClaim(String token, Function<Claims, T> claimResolver) throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Check whether the token is valid
     * @param token String
     * @param subject String
     * @return isTokenValid boolean
     */
    boolean isTokenValid(
            String token,
            String subject
    );
}
