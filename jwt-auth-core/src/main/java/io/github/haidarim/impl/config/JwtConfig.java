package io.github.haidarim.impl.config;

public final class JwtConfig {

    private String algorithm;
    private String hsSecret;
    private String rsPrivateKey;
    private String rsPublicKey;
    private boolean checkExpiration;
    private long expirationMillis;

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

    public void setAlgorithm(String algorithm){
        if("HS256".equals(algorithm) || "RSA".equals(algorithm)){
            this.algorithm = algorithm;
        }
        throw new RuntimeException("Invalid algorithm");
    }
    public void setCheckExpiration(boolean checkExpiration){
        this.checkExpiration = checkExpiration;
    }
    public void setExpirationMillis(long expirationMillis){
        this.expirationMillis = expirationMillis;
    }
}