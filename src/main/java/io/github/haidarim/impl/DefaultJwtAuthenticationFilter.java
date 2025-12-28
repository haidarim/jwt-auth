package io.github.haidarim.impl;

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

import static io.github.haidarim.api.JwtConstants.*;

@Component
@RequiredArgsConstructor
public class DefaultJwtAuthenticationFilter extends OncePerRequestFilter {

    private final DefaultJwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authenticationHeader = request.getHeader(AUTHENTICATION);
        final String token;
        final String subject;

        if (isHeaderNotValid(authenticationHeader)){
            filterChain.doFilter(request, response);
            return;
        }

        token = authenticationHeader.substring(BEARER_BEGIN_INDEX);
        try {
            subject = jwtService.getSubject(token);
            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(subject);
                if (jwtService.isTokenValid(token, userDetails, SET_EXPIRATION)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                      userDetails,
                      null,
                      userDetails.getAuthorities()
                    );

                    // set the request's detail as web auth detail for the token
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }catch (Exception e){
            // TODO LOG
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isHeaderNotValid(String authenticationHeader){
        return authenticationHeader == null || !authenticationHeader.startsWith(BEARER);
    }
}
