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

package io.github.haidarim.impl.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.impl.config.JwtConfig;


import javax.crypto.SecretKey;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultJwtService
 */
public class DefaultJwtServiceImpl implements JwtService {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultJwtServiceImpl.class);
    private final JwtConfig jwtConfig;

    private final String HS = "HS";
    private final String RSA = "RSA";

    /**
     * Constructor
     * @param jwtConfig JwtConfig
     */
    public DefaultJwtServiceImpl(JwtConfig jwtConfig){
        this.jwtConfig = jwtConfig;
    }

    @Override
    public <T> T getClaim(String token, Function<Claims, T> claimResolver) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final Claims claims = getAllClaims(token);
        return claimResolver.apply(claims);
    }

    @Override
    public String getSubject(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getClaim(token, Claims::getSubject);
    }

    @Override
    public String createToken(String subject, Map<String, Object> claims) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String jti = UUID.randomUUID().toString();
        claims.put("jti", jti);
        return createToken(claims, subject);
    }

    /**
     * Create token
     * @param extraClaims Map of String and object
     * @param subject Stirng
     * @return token String
     * @throws InvalidKeySpecException if key is invalid
     * @throws NoSuchAlgorithmException if algorithm is invalid
     */
    private String createToken(Map<String, Object> extraClaims, String subject) throws InvalidKeySpecException, NoSuchAlgorithmException {
        JwtBuilder jwtBuilder = Jwts
                .builder()
                .claims(extraClaims)
                .subject(subject);
        if(jwtConfig.isCheckExpiration()){
            jwtBuilder
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationMillis()));
        }
        jwtBuilder = jwtConfig.getAlgorithm().startsWith(HS) ? jwtBuilder.signWith(getSecretKey()) : jwtBuilder.signWith(getPrivateKey());
        return jwtBuilder.compact();
    }

    /**
     * Get all claims for the given token
     * @param token String
     * @return claims Claims
     * @throws NoSuchAlgorithmException if algorithm is invalid
     * @throws InvalidKeySpecException if key is invalid
     */
    private Claims getAllClaims(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JwtParserBuilder parser = Jwts.parser();
        parser = jwtConfig.getAlgorithm().startsWith(HS) ? parser.verifyWith(getSecretKey()) : parser.verifyWith(getPublicKey());
        return parser
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Getter method
     * @return secretKey SecretKey
     */
    private SecretKey getSecretKey() {
        final byte[] key = Decoders.BASE64.decode(jwtConfig.getHsSecret());
        return Keys.hmacShaKeyFor(key);
    }

    /**
     * Getter method
     * @return pubKey PublicKey
     * @throws NoSuchAlgorithmException if algorithm is invalid
     * @throws InvalidKeySpecException if key is invalid
     */
    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] key = Decoders.BASE64.decode(jwtConfig.getRsPublicKey());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(spec);
    }

    /**
     * Getter method
     * @return prKey PrivateKey
     * @throws InvalidKeySpecException if key is invalid
     * @throws NoSuchAlgorithmException if algorithm is invalid
     */
    private PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] key = Decoders.BASE64.decode(jwtConfig.getRsPrivateKey());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(spec);
    }

    @Override
    public boolean isTokenValid(String token, String providedSubject){
        try {
            String subject = getSubject(token);
            return subject.equals(providedSubject) && (!jwtConfig.isCheckExpiration() || isTokenStillValid(token));
        }catch (ExpiredJwtException e){
            return false;
        }
        catch (Exception e){
            LOGGER.error("Exception during token validation: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * check if token not expired
     * @param token String
     * @return isTokenStillValid boolean
     * @throws NoSuchAlgorithmException if algorithm is invalid
     * @throws InvalidKeySpecException if key is invalid
     */
    public boolean isTokenStillValid(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getClaim(token, Claims::getExpiration).after(new Date());
    }

    @Override
    public String getJti(String token) throws NoSuchAlgorithmException, InvalidKeySpecException{
        return getAllClaims(token).get("jti", String.class);
    }
}