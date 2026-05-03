/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.impl.config;

import io.github.haidarim.api.JwtAlgorithm;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * JwtConfig
 */
public final class JwtConfig {

    private JwtAlgorithm algorithm;
    private String hsSecret;
    private String rsPrivateKey;
    private String rsPublicKey;
    private boolean checkExpiration;
    private long expirationMillis;
    private long expirationJwtMargin;
    private Set<String> allowedIssuers = new HashSet<>();
    private Set<String> allowedAudiences = new HashSet<>();

    /**
     * Constructor
     * @param algorithm String
     * @param hsSecret String
     * @param rsPrivateKey String
     * @param rsPublicKey String
     * @param checkExpiration boolean
     * @param expirationMillis long
     */
    public JwtConfig(
            JwtAlgorithm algorithm,
            String hsSecret,
            String rsPrivateKey,
            String rsPublicKey,
            boolean checkExpiration,
            long expirationMillis,
            long expirationJwtMargin
    ) {
        this.algorithm = algorithm;
        this.hsSecret = hsSecret;
        this.rsPrivateKey = rsPrivateKey;
        this.rsPublicKey = rsPublicKey;
        this.checkExpiration = checkExpiration;
        this.expirationMillis = expirationMillis;
        this.expirationJwtMargin = expirationJwtMargin;
    }

    /**
     * Getter method
     * @return algorithm String
     */
    public JwtAlgorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Getter method
     * @return hsSecret String
     */
    public String getHsSecret() {
        return hsSecret;
    }

    /**
     * Getter method
     * @return rsPrivateKey String
     */
    public String getRsPrivateKey() {
        return rsPrivateKey;
    }

    /**
     * Getter method
     * @return rsPublicKey String
     */
    public String getRsPublicKey() {
        return rsPublicKey;
    }

    /**
     * Is expiration check on
     * @return value boolean
     */
    public boolean isCheckExpiration() {
        return checkExpiration;
    }

    /**
     * Getter method
     * @return expirationMillis long
     */
    public long getExpirationMillis() {
        return expirationMillis;
    }

    /**
     * Setter method
     * @param algorithm String
     */
    public void setAlgorithm(JwtAlgorithm algorithm){
        this.algorithm = Objects.requireNonNull(algorithm);
    }

    /**
     * Setter mthod
     * @param checkExpiration boolean
     */
    public void setCheckExpiration(boolean checkExpiration){
        this.checkExpiration = checkExpiration;
    }

    /**
     * Setter method
     * @param expirationMillis long
     */
    public void setExpirationMillis(long expirationMillis){
        this.expirationMillis = expirationMillis;
    }

    /**
     * Get expiration margin
     * @return margin long
     */
    public long getExpirationJwtMargin(){
        return expirationJwtMargin;
    }

    /**
     * Setter for expirationJwtMargin
     * @param expirationJwtMargin long
     */
    public void setExpirationJwtMargin(long expirationJwtMargin){
        this.expirationJwtMargin = expirationJwtMargin;
    }

    /**
     * Add issuer to allowedIssuers
     * @param issuer String
     */
    public void addIssuer(String issuer){
        this.allowedIssuers.add(issuer);
    }

    /**
     * Add audience ti allowedAudiences
     * @param audience String
     */
    public void addAudience(String audience){
        this.allowedAudiences.add(audience);
    }

    /**
     * Checks whether the issuer is valid
     * @param issuer String
     * @return isIssuerValid boolean
     */
    public boolean isIssuerValid(String issuer){
        return allowedIssuers.contains(issuer);
    }

    /**
     * Checks whether the audience is valid
     * @param audience String
     * @return isAudienceValid boolean
     */
    public boolean isAudienceValid(String audience){
        return allowedAudiences.contains(audience);
    }
}