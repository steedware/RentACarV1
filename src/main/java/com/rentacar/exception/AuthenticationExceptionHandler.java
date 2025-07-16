package com.rentacar.exception;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class AuthenticationExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException e, Model model) {
        log.error("Authentication error: {}", e.getMessage(), e);
        model.addAttribute("errorMessage", "Authentication failed: " + e.getMessage());
        return "login";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e, Model model) {
        log.error("Access denied: {}", e.getMessage(), e);
        model.addAttribute("errorMessage", "Access denied: " + e.getMessage());
        return "error/forbidden";
    }
    
    @ExceptionHandler(ServletException.class)
    public String handleServletException(ServletException e, Model model) {
        log.error("Servlet error during authentication: {}", e.getMessage(), e);
        model.addAttribute("errorMessage", "Server error during authentication: " + e.getMessage());
        return "error/error";
    }
}
