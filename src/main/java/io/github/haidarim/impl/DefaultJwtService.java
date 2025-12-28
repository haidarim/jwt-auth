package io.github.haidarim.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.github.haidarim.api.JwtConstants.*;

@Service
@RequiredArgsConstructor
public class DefaultJwtService {
    @Value("${jwt-key-algo:HS256}")
    private final String ALGORITHM;
    @Value("${hs_secret:}")
    private final String HS_SECRET;
    @Value("${rs_pr_secret:}")
    private final String RS_PR_SECRET;
    @Value("${rs_pub_secret:}")
    private final String RS_PUB_SECRET;

//    public JwtService(
//            @Value("${jwt-key-algo:HS256}") String algorithm,
//            @Value("${hs_secret:}") String hsSecret,
//            @Value("${rs_pr_secret:}") String rsPrivateKey,
//            @Value("${rs_pub_secret:}") String rsPublicKey
//    ) {
//        this.ALGORITHM = algorithm;
//        this.HS_SECRET= hsSecret;
//        this.RS_PR_SECRET = rsPrivateKey;
//        this.RS_PUB_SECRET = rsPublicKey;
//    }

    public <T> T getClaim(String token, Function<Claims, T> claimResolver) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final Claims claims = getAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String getSubject(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getClaim(token, Claims::getSubject);
    }

    public String createToken(UserDetails userDetails, boolean setExpiration) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return createToken(new HashMap<>(), userDetails, setExpiration);
    }

    public String createToken(Map<String, Object> extraClaims, UserDetails userDetails, boolean setExpiration) throws InvalidKeySpecException, NoSuchAlgorithmException {
        JwtBuilder jwtBuilder = Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername());
        if(setExpiration){
            jwtBuilder
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME));
        }
        jwtBuilder = ALGORITHM.startsWith(HS) ? jwtBuilder.signWith(getSecretKey()) : jwtBuilder.signWith(getPrivateKey());
        return jwtBuilder.compact();
    }

    private Claims getAllClaims(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JwtParserBuilder parser = Jwts.parser();
        parser = ALGORITHM.startsWith(HS) ? parser.verifyWith(getSecretKey()) : parser.verifyWith(getPublicKey());
        return parser
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        final byte[] key = Decoders.BASE64.decode(HS_SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] key = Decoders.BASE64.decode(RS_PUB_SECRET);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(spec);
    }

    private PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] key = Decoders.BASE64.decode(RS_PR_SECRET);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(spec);
    }

    public boolean isTokenValid(String token, UserDetails userDetails, boolean checkExpiration){
        try {
            String subject = getSubject(token);
            return checkExpiration ? (subject.equals(userDetails.getUsername())) : (subject.equals(userDetails.getUsername()) && isTokenNotExpired(token));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean isTokenNotExpired(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getClaim(token, Claims::getExpiration).before(new Date());
    }
}
