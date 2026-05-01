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

package io.github.haidarim.impl;

import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.api.service.TokenRevocationService;
import io.github.haidarim.properties.JwtAuthProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultJwtAuthenticationFilter
 */
@NullMarked
@Component
@RequiredArgsConstructor
public class DefaultJwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultJwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtAuthProperties jwtAuthProperties;
    private final TokenRevocationService tokenRevocationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authenticationHeader = request.getHeader(jwtAuthProperties.getHeader());
        final String token;
        final String subject;

        if (isHeaderNotValid(authenticationHeader)){
            filterChain.doFilter(request, response);
            return;
        }
        LOGGER.debug("Authorization header present");
        token = authenticationHeader.substring(jwtAuthProperties.getBearerPrefix().length()).trim();

        if (token.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            subject = jwtService.getSubject(token);
            if (isTokenValid(subject, token)) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(subject);
                LOGGER.debug("JWT token is valid");
                LOGGER.debug("Authorities: {}", userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // set the request's detail as web auth detail for the token
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                LOGGER.debug("Updating SecurityContextHolder");
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                LOGGER.debug("SecurityContextHolder auth: {}", SecurityContextHolder.getContext().getAuthentication());
            }
        }catch (ExpiredJwtException e) {
            LOGGER.debug("Token expired", e);
        } catch (JwtException e) {
            LOGGER.debug("Invalid JWT", e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.warn("Crypto configuration error", e);
        }catch (UsernameNotFoundException e){
            LOGGER.warn("User detail not found error", e);
        }
        
        LOGGER.info("End of filter process for path: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    private boolean isHeaderNotValid(String authenticationHeader){
        return authenticationHeader == null || !authenticationHeader.startsWith(jwtAuthProperties.getBearerPrefix());
    }

    private boolean isTokenValid(String subject, String token) {
        try{
            return SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtService.isTokenValid(token, subject)
                    && !tokenRevocationService.isTokenRevoked(jwtService.getJti(token));
        }catch (InvalidKeySpecException | NoSuchAlgorithmException e){
         return false;
        }
    }
}