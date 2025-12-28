package io.github.haidarim.impl.config;

import io.github.haidarim.repository.UserDetailsRepository;
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

@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnClass(DefaultSecurityConfiguration.class)
public class DefaultAppConfiguration {

    private final UserDetailsRepository userDetailsRepository;

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsService userDetailsService(){
        return userDetailsRepository::findByUsername;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
