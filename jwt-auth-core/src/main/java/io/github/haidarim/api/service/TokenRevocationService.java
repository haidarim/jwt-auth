package io.github.haidarim.api.service;


public interface TokenRevocationService {


    void revokeToken(String jti, long expiresAt);

    boolean isTokenRevoked(String jti);

    int deleteExpiredTokens();
}
