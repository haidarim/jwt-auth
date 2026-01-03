package io.github.haidarim.properties;



import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt-auth")
public class JwtAuthProperties {
    private String header = "Authentication";
    private String bearerPrefix = "Bearer";
    private int bearerBeginIndex = 7;
    private String[] whiteList = {"/api/v0/auth/register", "/api/v0/auth/authenticate"};

    public void setBearerPrefix(String bearerPrefix) {
        this.bearerPrefix = bearerPrefix;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setBearerBeginIndex(int bearerBeginIndex) {
        this.bearerBeginIndex = bearerBeginIndex;
    }

    public void setWhiteList(String[] whiteList) {
        this.whiteList = whiteList;
    }

    public int getBearerBeginIndex() {
        return bearerBeginIndex;
    }

    public String getHeader() {
        return header;
    }

    public String getBearerPrefix() {
        return bearerPrefix;
    }

    public String[] getWhiteList() {
        return whiteList;
    }


}

