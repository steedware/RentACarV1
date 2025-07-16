package com.rentacar.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = Logger.getLogger(LoggingFilter.class.getName());
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            logger.info("Request URI: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            logger.info("Response status: " + response.getStatus());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during request processing: " + request.getRequestURI(), e);
            throw e;
        }
    }
}
