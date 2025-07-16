package com.rentacar.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    
    private static final Logger LOGGER = Logger.getLogger(ErrorController.class.getName());

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        
        if (exception != null) {
            LOGGER.log(Level.SEVERE, "Error occurred", exception);
            model.addAttribute("errorMessage", exception.getMessage());
        } else {
            model.addAttribute("errorMessage", "An unexpected error occurred");
        }
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/not-found";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "error/bad-request";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/forbidden";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return "error/unauthorized";
            }
        }
        
        return "error/error";
    }
}
