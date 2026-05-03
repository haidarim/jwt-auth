/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.common;

import io.github.haidarim.api.JwtAlgorithm;
import io.github.haidarim.api.Role;
import io.github.haidarim.api.dto.request.AuthenticationRequest;
import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.api.service.JwtAuthenticationService;
import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.api.service.TokenRevocationService;
import io.github.haidarim.entity.TestUser;
import io.github.haidarim.impl.config.JwtConfig;
import io.github.haidarim.repository.TestUserRepository;
import io.github.haidarim.service.TestUserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Optional;

import static io.github.haidarim.common.TestConstants.VALID_AUDIENCE;
import static io.github.haidarim.common.TestConstants.VALID_ISSUER;

@RequiredArgsConstructor
@TestConfiguration(proxyBeanMethods = false)
public class TestAppConfig {

    private final TestUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRevocationService tokenRevocationService;

    @Bean
    public UserDetailsService userDetailsService(){
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public JwtAuthenticationService jwtAuthenticationService(JwtConfig jwtConfig, JwtService jwtService, AuthenticationManager authenticationManager){
        return new JwtAuthenticationService() {
            @Override
            public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception{
                String email = request.getEmail() != null && !request.getEmail().trim().isEmpty()? request.getEmail()
                        : request.getUsername() != null && !request.getUsername().trim().isEmpty()? userRepository.findEmailByUsername(request.getUsername()).orElse("")
                        : request.getUniqueNumber() != null && !request.getUniqueNumber().trim().isEmpty()? userRepository.findEmailByUniqueNumber(request.getUniqueNumber()).orElse("")
                        :"";
                if (email.isEmpty()){
                    throw new RuntimeException("Email not provided");
                }
                if(!jwtConfig.isIssuerValid(request.getIssuer()) || !jwtConfig.isAudienceValid(request.getAudience())){
                    throw new RuntimeException("Email not provided");
                }
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getPassword()
                        )
                );

                Optional<TestUser> user = userRepository.findByEmail(email);
                String token = jwtService.createToken(user.get().getEmail(), new HashMap<>(), request.getIssuer(), request.getAudience());
                return AuthenticationResponse
                        .builder()
                        .token(token)
                        .build();
            }

            @Override
            public AuthenticationResponse register(RegisterRequest request) throws Exception {
                Optional<TestUser> user = userRepository.findByEmail(request.getEmail());
                if(!jwtConfig.isIssuerValid(request.getIssuer()) || !jwtConfig.isAudienceValid(request.getAudience())){
                    throw new RuntimeException("Email not provided");
                }
                String token = jwtService.createToken(user.get().getEmail(), new HashMap<>(), request.getIssuer(), request.getAudience());
                return AuthenticationResponse
                        .builder()
                        .token(token)
                        .build();
            }

            @Override
            public void revokeToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {

                String jti = jwtService.getClaim(token, Claims::getId);
                long exp = jwtService.getClaim(token, Claims::getExpiration).getTime();

                tokenRevocationService.revokeToken(jti, exp);
            }
        };
    }

    @Bean
    public TestUserService testUserService(){
        return new TestUserService() {
            @Override
            public TestUser createOrUpdateUser(String username, String email, String password, String uniqueNumber, String salt) {
                TestUser user = TestUser
                        .builder()
                        .username(username)
                        .email(email)
                        .passwordHash(passwordEncoder.encode(password))
                        .uniqueNumber(uniqueNumber)
                        .salt(salt)
                        .role(Role.USER)
                        .build();
                userRepository.save(user);
                return user;
            }

            @Override
            public void deleteUser(String email) {
                Optional<TestUser> user = userRepository.findByEmail(email);
                userRepository.delete(user.get());
            }
        };
    }

    // JwtBecame a bean now from POJO
    @Bean
    public JwtConfig jwtConfig(
            @Value("${jwt.algorithm}") JwtAlgorithm algorithm,
            @Value("${jwt.hs_secret}") String hsSecret,
            @Value("${jwt.pr_secret}") String privateKey,
            @Value("${jwt.pub_secret}") String publicKey,
            @Value("${jwt.check_expiration}") boolean checkExpiration,
            @Value("${jwt.expiration_time}") long expirationMillis,
            @Value("${jwt.expiration_jwt_margin}") long expirationJwtMargin
    ) {
        JwtConfig jwtConfig = new JwtConfig(
                algorithm,
                hsSecret,
                privateKey,
                publicKey,
                checkExpiration,
                expirationMillis,
                expirationJwtMargin
        );

        jwtConfig.addIssuer(VALID_ISSUER);
        jwtConfig.addAudience(VALID_AUDIENCE);
        return jwtConfig;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            // trivial auth for testing
            return authentication;
        };
    }
}
