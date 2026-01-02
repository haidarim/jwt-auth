package io.github.haidarim.config;

import io.github.haidarim.impl.DefaultJwtAuthenticationFilter;
import io.github.haidarim.properties.JwtAuthProperties;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@AutoConfiguration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnClass({DefaultJwtAuthenticationFilter.class, AuthenticationProvider.class})
public class DefaultSecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final DefaultJwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthProperties jwtProperties;

    @Bean
    @ConditionalOnMissingBean // to let be customized
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
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