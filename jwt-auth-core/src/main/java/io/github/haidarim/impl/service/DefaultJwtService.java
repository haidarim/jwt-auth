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
import java.util.function.Function;

public class DefaultJwtService implements JwtService {

    private final JwtConfig jwtConfig;

    private final String HS = "HS";
    private final String RSA = "RSA";

    public DefaultJwtService(JwtConfig jwtConfig){
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
    public String createToken(String username) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return createToken(new HashMap<>(), username);
    }

    public String createToken(Map<String, Object> extraClaims, String username) throws InvalidKeySpecException, NoSuchAlgorithmException {
        JwtBuilder jwtBuilder = Jwts
                .builder()
                .claims(extraClaims)
                .subject(username);
        if(jwtConfig.isCheckExpiration()){
            jwtBuilder
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationMillis()));
        }
        jwtBuilder = jwtConfig.getAlgorithm().startsWith(HS) ? jwtBuilder.signWith(getSecretKey()) : jwtBuilder.signWith(getPrivateKey());
        return jwtBuilder.compact();
    }

    private Claims getAllClaims(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JwtParserBuilder parser = Jwts.parser();
        parser = jwtConfig.getAlgorithm().startsWith(HS) ? parser.verifyWith(getSecretKey()) : parser.verifyWith(getPublicKey());
        return parser
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        final byte[] key = Decoders.BASE64.decode(jwtConfig.getHsSecret());
        return Keys.hmacShaKeyFor(key);
    }

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] key = Decoders.BASE64.decode(jwtConfig.getRsPublicKey());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(spec);
    }

    private PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] key = Decoders.BASE64.decode(jwtConfig.getRsPrivateKey());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(spec);
    }

    @Override
    public boolean isTokenValid(String token, String username){
        try {
            String subject = getSubject(token);
            return jwtConfig.isCheckExpiration() ? (subject.equals(username)) : (subject.equals(username) && isTokenNotExpired(token));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean isTokenNotExpired(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getClaim(token, Claims::getExpiration).before(new Date());
    }
}