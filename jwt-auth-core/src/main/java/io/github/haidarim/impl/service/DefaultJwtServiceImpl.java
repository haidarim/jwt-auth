/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.impl.service;

import io.github.haidarim.api.JwtAlgorithm;
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
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.haidarim.api.JwtAlgorithm.HS256;
import static io.github.haidarim.api.JwtAlgorithm.RSA;

/**
 * DefaultJwtService
 */
public class DefaultJwtServiceImpl implements JwtService {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultJwtServiceImpl.class);
    private final JwtConfig jwtConfig;

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
    public String createToken(String subject, Map<String, Object> claims, String issuer, String audience) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return createToken(claims, subject, issuer, audience);
    }

    /**
     * Create token
     * @param extraClaims Map of String and object
     * @param subject Stirng
     * @return token String
     * @throws InvalidKeySpecException if key is invalid
     * @throws NoSuchAlgorithmException if algorithm is invalid
     */
    private String createToken(Map<String, Object> extraClaims, String subject, String issuer, String audience) throws InvalidKeySpecException, NoSuchAlgorithmException {
        JwtBuilder jwtBuilder = Jwts
                .builder()
                .claims(extraClaims)
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .audience().add(audience)
                .and()
                .subject(subject);
        if(jwtConfig.isCheckExpiration()){
            Instant now = Instant.now();
            jwtBuilder
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plusMillis(jwtConfig.getExpirationMillis())));
        }
        validateAlgorithm(jwtConfig.getAlgorithm());
        jwtBuilder = switch (jwtConfig.getAlgorithm()) {
            case HS256 -> jwtBuilder.signWith(getSecretKey());
            case RSA -> jwtBuilder.signWith(getPrivateKey());
        };
        return jwtBuilder.compact();
    }

    /**
     * Get all claims for the given token
     * @param token String
     * @return claims Claims
     * @throws NoSuchAlgorithmException if algorithm is invalid
     * @throws InvalidKeySpecException if key is invalid
     */
    @Override
    public Claims getAllClaims(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JwtParserBuilder parser = Jwts.parser();
        validateAlgorithm(jwtConfig.getAlgorithm());
        parser = switch (jwtConfig.getAlgorithm()){
            case HS256 -> parser.verifyWith(getSecretKey());
            case RSA -> parser.verifyWith(getPublicKey());
        };
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
        KeyFactory keyFactory = KeyFactory.getInstance(RSA.name());
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
        KeyFactory keyFactory = KeyFactory.getInstance(RSA.name());
        return keyFactory.generatePrivate(spec);
    }

    @Override
    public boolean isTokenValid(String token, String providedSubject){
        try {
            Claims claims = getAllClaims(token);
            String subject = claims.getSubject();

            return subject.equals(providedSubject)
                    && (!jwtConfig.isCheckExpiration() || isTokenStillValid(token))
                    && jwtConfig.isIssuerValid(claims.getIssuer())
                    && claims.getAudience().stream().allMatch(jwtConfig::isAudienceValid);
        }catch (ExpiredJwtException e){
            return false;
        }
        catch (Exception e){
            LOGGER.error("Exception during token validation: {}", e.getMessage());
            return false;
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
        Instant now = Instant.now();
        return getClaim(token, Claims::getExpiration).toInstant().isAfter(now.plusSeconds(jwtConfig.getExpirationJwtMargin()));
    }

    /**
     * Validate given algorithm
     * @param algorithm JwtAlgorithm
     * @throws NoSuchAlgorithmException if algorithm is not supported
     */
    protected void validateAlgorithm(JwtAlgorithm algorithm) throws NoSuchAlgorithmException {
        if (!(RSA.equals(algorithm) || HS256.equals(algorithm))){
            throw new NoSuchAlgorithmException("Unsupported algorithm");
        }
    }
}