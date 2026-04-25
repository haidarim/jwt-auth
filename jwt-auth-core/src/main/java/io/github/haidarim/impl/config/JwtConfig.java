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

package io.github.haidarim.impl.config;

/**
 * JwtConfig
 */
public final class JwtConfig {

    private String algorithm;
    private String hsSecret;
    private String rsPrivateKey;
    private String rsPublicKey;
    private boolean checkExpiration;
    private long expirationMillis;

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
            String algorithm,
            String hsSecret,
            String rsPrivateKey,
            String rsPublicKey,
            boolean checkExpiration,
            long expirationMillis
    ) {
        this.algorithm = algorithm;
        this.hsSecret = hsSecret;
        this.rsPrivateKey = rsPrivateKey;
        this.rsPublicKey = rsPublicKey;
        this.checkExpiration = checkExpiration;
        this.expirationMillis = expirationMillis;
    }

    /**
     * Getter method
     * @return algorithm String
     */
    public String getAlgorithm() {
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
    public void setAlgorithm(String algorithm){
        if("HS256".equals(algorithm) || "RSA".equals(algorithm)){
            this.algorithm = algorithm;
            return;
        }
        throw new RuntimeException("Invalid algorithm");
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
}