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

package io.github.haidarim.properties;



import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JwtAuthProperties
 */
@ConfigurationProperties(prefix = "jwt-auth")
public class JwtAuthProperties {
    private String header = "Authentication";
    private String bearerPrefix = "Bearer ";
    private int bearerBeginIndex = 7;
    private String[] whiteList = {"/api/v0/auth/register", "/api/v0/auth/authenticate"};

    /**
     * BearerPrefix
     * @param bearerPrefix String
     */
    public void setBearerPrefix(String bearerPrefix) {
        this.bearerPrefix = bearerPrefix;
    }

    /**
     * Setter method
     * @param header String
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Setter method
     * @param bearerBeginIndex int
     */
    public void setBearerBeginIndex(int bearerBeginIndex) {
        this.bearerBeginIndex = bearerBeginIndex;
    }

    /**
     * Setter method
     * @param whiteList String array
     */
    public void setWhiteList(String[] whiteList) {
        this.whiteList = whiteList;
    }

    /**
     * Getter method
     * @return bearerBeginIndex int
     */
    public int getBearerBeginIndex() {
        return bearerBeginIndex;
    }

    /**
     * Getter method
     * @return header String
     */
    public String getHeader() {
        return header;
    }

    /**
     * Getter method
     * @return bearerPrefix String
     */
    public String getBearerPrefix() {
        return bearerPrefix;
    }

    /**
     * Getter method
     * @return whiteList String array
     */
    public String[] getWhiteList() {
        return whiteList;
    }


}

