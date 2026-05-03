/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.filter;

import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.api.service.TokenRevocationService;
import io.github.haidarim.api.JwtAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultJwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtAuthProperties jwtAuthProperties;
    @Mock
    private TokenRevocationService tokenRevocationService;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private DefaultJwtAuthenticationFilter filter;

    @BeforeEach
    void setUp(){
        filter = new DefaultJwtAuthenticationFilter(jwtService, userDetailsService, tokenRevocationService);
    }

    @Test
    void filterWithNullAuthenticationHeaderTest() throws Exception{
        when(request.getHeader(any())).thenReturn(null);
        filter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void filterWithInvalidAuthenticationHeader() throws Exception{
        when(request.getHeader(any())).thenReturn("BBB");
        filter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }
}
