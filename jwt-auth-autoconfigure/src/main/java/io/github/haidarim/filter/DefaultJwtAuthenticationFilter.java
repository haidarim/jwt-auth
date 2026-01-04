package io.github.haidarim.impl;

import io.github.haidarim.impl.service.DefaultJwtService;
import io.github.haidarim.properties.JwtAuthProperties;
import jakarta.servlet.ServletException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class DefaultJwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultJwtAuthenticationFilter.class);
    private final DefaultJwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtAuthProperties jwtAuthProperties;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authenticationHeader = request.getHeader(jwtAuthProperties.getHeader());
        final String token;
        final String subject;

        LOGGER.info("authenticationHeader: {}", authenticationHeader);

        if (isHeaderNotValid(authenticationHeader)){
            filterChain.doFilter(request, response);
            return;
        }

        token = authenticationHeader.substring(jwtAuthProperties.getBearerPrefix().length());
        try {
            subject = jwtService.getSubject(token);
            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(subject);
                if (jwtService.isTokenValid(token, subject)){
                    LOGGER.info("token is valid");
                    LOGGER.info("Authorities: {}", userDetails.getAuthorities());
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // set the request's detail as web auth detail for the token
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    LOGGER.info("Updating SecurityContextHolder");
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    LOGGER.info("SecurityContextHolder auth: {}", SecurityContextHolder.getContext().getAuthentication());
                }
            }
        }catch (Exception e){
            LOGGER.info("Exception during execution of filter: {}", e.getMessage());
        }
        LOGGER.info("End of filter process for path: {}, token: {}", request.getPathInfo(), token);
        filterChain.doFilter(request, response);
    }

    private boolean isHeaderNotValid(String authenticationHeader){
        return authenticationHeader == null || !authenticationHeader.startsWith(jwtAuthProperties.getBearerPrefix());
    }
}