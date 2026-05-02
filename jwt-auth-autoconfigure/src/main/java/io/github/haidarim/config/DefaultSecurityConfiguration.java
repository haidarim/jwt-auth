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

import io.github.haidarim.filter.DefaultJwtAuthenticationFilter;
import io.github.haidarim.api.JwtAuthProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

import static io.github.haidarim.api.JwtAuthProperties.WHITE_LIST;

/**
 * DefaultSecurityConfiguration
 * provides {@link SecurityFilterChain}
 */
@AutoConfiguration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnClass({DefaultJwtAuthenticationFilter.class, AuthenticationProvider.class})
public class DefaultSecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final DefaultJwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * default {@link SecurityFilterChain}
     * @param http {@link HttpSecurity}
     * @return securityFilterChain {@link SecurityFilterChain}
     */
    @Bean
    @ConditionalOnMissingBean // to let be customized
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http){
        http
                // We are using jwt so Cross-Site Request Forgery protection is not needed
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(WHITE_LIST)
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
                // execute JwtFilter after user pass check
                .addFilterAfter(jwtAuthenticationFilter, org.springframework.security.web.access.ExceptionTranslationFilter.class);
        return  http.build();
    }
}