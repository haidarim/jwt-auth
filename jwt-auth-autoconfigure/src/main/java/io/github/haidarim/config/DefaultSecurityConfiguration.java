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

package io.github.haidarim.config;

import io.github.haidarim.impl.DefaultJwtAuthenticationFilter;
import io.github.haidarim.properties.JwtAuthProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * DefaultSecurityConfiguration
 * provides {@link SecurityFilterChain}
 */
@AutoConfiguration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnClass({DefaultJwtAuthenticationFilter.class, AuthenticationProvider.class})
public class DefaultSecurityConfiguration {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultSecurityConfiguration.class);
    private final AuthenticationProvider authenticationProvider;
    private final DefaultJwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthProperties jwtProperties;

    /**
     * default {@link SecurityFilterChain}
     * @param http {@link HttpSecurity}
     * @return securityFilterChain {@link SecurityFilterChain}
     * @throws Exception if any validation fails
     */
    @Bean
    @ConditionalOnMissingBean // to let be customized
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        LOGGER.info("#### SecurityFilterChain ####");
        http
                // We are using jwt so Cross-Site Request Forgery protection is not needed
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(jwtProperties.getWhiteList())
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session
                        // configure to not store the session, authenticate each session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                )
                .authenticationProvider(authenticationProvider)
                // execute JwtFilter before user pass check
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return  http.build();
    }
}