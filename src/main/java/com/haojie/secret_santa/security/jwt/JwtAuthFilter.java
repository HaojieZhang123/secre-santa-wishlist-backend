package com.haojie.secret_santa.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.haojie.secret_santa.security.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // service with JWT logic: generate, validate, extract info
    private final JwtUtils jwtUtils;

    // userDetailsService to load user by username
    private final UserDetailsServiceImpl userDetailsService;

    // logger
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // get Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // check if header is valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // continue filter chain
            filterChain.doFilter(request, response);
            return;
        }

        // extract token
        jwt = authHeader.substring(7);

        try {
            // extract username
            username = jwtUtils.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // load user details
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // validate token
                if (jwtUtils.validateJwtToken(jwt, userDetails)) {
                    // create new authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    // set web details to the token
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // set authentication to security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    // SecurityContextHolder define SecurityContext
                    // memorize the valid authentication

                    // everytime we need to get UserDetails, use SecurityContext:

                    // UserDetails userDetails = (UserDetails)
                    // SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                    // userDetails.getUsername();
                    // userDetails.getPassword();
                    // userDetails.getAuthorities();
                }
            }
        } catch (Exception e) {
            // invalid token
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // continue filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // do not filter /api/auth/** endpoints
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || path.startsWith("/h2-console/") || path.startsWith("/css/")
                || path.startsWith("/js/");
    }

}