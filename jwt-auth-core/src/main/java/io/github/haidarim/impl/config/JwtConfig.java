package io.github.haidarim.impl.config;

public final class JwtConfig {

    private final String algorithm;
    private final String hsSecret;
    private final String rsPrivateKey;
    private final String rsPublicKey;
    private final boolean checkExpiration;
    private final long expirationMillis;

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

    public String getAlgorithm() {
        return algorithm;
    }

    public String getHsSecret() {
        return hsSecret;
    }

    public String getRsPrivateKey() {
        return rsPrivateKey;
    }

    public String getRsPublicKey() {
        return rsPublicKey;
    }

    public boolean isCheckExpiration() {
        return checkExpiration;
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }
}