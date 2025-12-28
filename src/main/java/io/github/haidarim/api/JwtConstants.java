package io.github.haidarim.api;

public interface JwtConstants {

    String AUTHENTICATION = "Authentication";
    String BEARER = "Bearer";
    int BEARER_BEGIN_INDEX = 7;
    String HS = "HS";
    String RS = "RS";
    String RSA = "RSA";
    long HOUR = 5;
    long MINUTE = 45;
    long SECOND = 5;
    long EXPIRATION_TIME = 1000L * (HOUR * 60 * 60 + MINUTE * 60 + SECOND);
    boolean SET_EXPIRATION = true;
    String[] WHITE_LIST = {"api-v0/sign-on", "api-v0/authenticate"};
}
