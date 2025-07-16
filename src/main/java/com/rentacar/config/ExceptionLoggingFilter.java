package com.rentacar.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ExceptionLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingFilter.class);
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Exception during filter chain execution: {}", e.getMessage(), e);
            throw e; // Re-throw the exception after logging it
        }
    }
}
