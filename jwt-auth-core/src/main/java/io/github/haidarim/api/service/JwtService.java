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
