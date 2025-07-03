package com.fastpay.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (token != null) {
            try {
                // Vulnerability: Basic token validation without proper verification
                if (jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    
                    // Vulnerability: No additional security checks
                    // Missing: token expiry validation, blacklist check, scope validation
                    
                    // Create authentication without proper validation
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Vulnerability: Detailed error information disclosure
                logger.error("JWT validation failed: " + e.getMessage() + " for token: " + token);
                response.setStatus(401);
                response.getWriter().write("Authentication failed: " + e.getMessage());
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        // Vulnerability: Multiple token sources without validation
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Vulnerability: Accept token from query parameter (insecure)
        String tokenFromQuery = request.getParameter("token");
        if (tokenFromQuery != null) {
            return tokenFromQuery;
        }
        
        // Vulnerability: Accept token from custom header (no validation)
        String customToken = request.getHeader("X-Auth-Token");
        if (customToken != null) {
            return customToken;
        }
        
        return null;
    }
}
