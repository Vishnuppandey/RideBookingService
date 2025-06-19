package com.user.service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header
        String authHeader = request.getHeader("Authorization");

        // If Authorization header is missing or doesn't contain "Bearer", continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No JWT token found in request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token from the Authorization header
        String token = authHeader.substring(7);
        String username;

        try {
            // Extract username from token
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // If the username is invalid, continue the filter chain
        if (username == null) {
            logger.error("Invalid JWT token: username is null");
            filterChain.doFilter(request, response);
            return;
        }

        // Check if authentication is already set
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate the token
            if (jwtService.validateToken(token, userDetails)) {
                // Set authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("JWT authentication successful for user: {}", username);
            } else {
                logger.warn("JWT validation failed for token: {}", token);
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
