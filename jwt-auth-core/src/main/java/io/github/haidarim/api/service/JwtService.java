package io.github.haidarim.api.service;

import io.jsonwebtoken.Claims;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.function.Function;


public interface JwtService {
    String createToken(String username) throws NoSuchAlgorithmException, InvalidKeySpecException;

    String getSubject(String token)
            throws NoSuchAlgorithmException, InvalidKeySpecException;

    <T> T getClaim(String token, Function<Claims, T> claimResolver) throws NoSuchAlgorithmException, InvalidKeySpecException;

    boolean isTokenValid(
            String token,
            String subject
    );
}
