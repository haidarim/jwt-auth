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

package io.github.haidarim.controller;

import io.github.haidarim.api.dto.request.AuthenticationRequest;
import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.api.service.JwtAuthenticationService;
import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.service.TestUserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v0/auth")
@RequiredArgsConstructor
public class TestAuthenticationController {
    private final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtAuthenticationService authenticationService;
    private final JwtService jwtService;
    private final TestUserService userService;

    @PostMapping("/register")
    public ResponseEntity<@NotNull AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        try {
            userService.createOrUpdateUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getUniqueNumber(),
                    "DUMMY_SALT"
            );
            return ResponseEntity.ok(authenticationService.register(request));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<@NotNull AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        }catch (Exception e){
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).build();
        }
    }

    @GetMapping("/get/hello-message")
    public String getHelloMessage(){
        return "Hello!";
    }

    @PostMapping("/logout")
    public ResponseEntity<@NotNull String> logOut(HttpServletRequest request){
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing token");
        }
        try {
            String token = authHeader.substring(7);
            authenticationService.revokeToken(token);
            return ResponseEntity.ok("Logged out successfully");
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return ResponseEntity.ok("Logged out failed");
        }
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<@NotNull Void> deleteCurrentUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
}
