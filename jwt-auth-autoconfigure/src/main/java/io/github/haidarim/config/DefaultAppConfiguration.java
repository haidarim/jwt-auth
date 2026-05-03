/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * DefaultAppConfiguration,
 * provides:
 * {@link AuthenticationProvider}
 * {@link AuthenticationManager}
 * {@link PasswordEncoder}
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnClass(DefaultSecurityConfiguration.class)
public class DefaultAppConfiguration {

    /**
     * Default AuthenticationProvider bean
     * @param userDetailsService UserDetailsService
     * @return authenticationProvider {@link AuthenticationProvider}
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Default AuthenticationManager bean
     * @param configuration {@link AuthenticationConfiguration}
     * @return authenticationManager {@link AuthenticationManager}
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }

    /**
     * Default PasswordEncoder
     * @return passwordEncoder {@link PasswordEncoder}
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}