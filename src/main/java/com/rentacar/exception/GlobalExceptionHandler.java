package com.rentacar.exception;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model) {
        logger.log(Level.SEVERE, "Unexpected error", e);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("exception", e);
        return "error/error";
    }
    
    // Special handler for ServletExceptions to provide more detailed logs
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServletException(ServletException e, Model model) {
        logger.log(Level.SEVERE, "Servlet execution error", e);
        model.addAttribute("errorMessage", "A server error occurred while processing your request.");
        model.addAttribute("exception", e);
        return "error/error";
    }
    
    // Add handler for file upload size exceptions
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, Model model) {
        logger.log(Level.WARNING, "File upload too large", e);
        model.addAttribute("errorMessage", "Uploaded file is too large. Maximum size allowed is 10MB.");
        return "error/bad-request";
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        logger.log(Level.WARNING, "Invalid argument", e);
        model.addAttribute("errorMessage", e.getMessage());
        return "error/bad-request";
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException e, Model model) {
        logger.log(Level.WARNING, "Resource not found", e);
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("resourceId", e.getResourceId());
        model.addAttribute("resourceType", e.getResourceType());
        return "error/not-found";
    }
    
    @ExceptionHandler(ReservationException.class)
    public String handleReservationException(ReservationException e, RedirectAttributes redirectAttributes) {
        logger.log(Level.WARNING, "Reservation error", e);
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        
        if (e.getVehicleId() != null) {
            return "redirect:/vehicles/" + e.getVehicleId();
        } else {
            return "redirect:/vehicles";
        }
    }
    
    @ExceptionHandler(PaymentException.class)
    public String handlePaymentException(PaymentException e, RedirectAttributes redirectAttributes) {
        logger.log(Level.WARNING, "Payment error", e);
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        
        if (e.getReservationId() != null) {
            return "redirect:/reservations/payment/" + e.getReservationId();
        } else {
            return "redirect:/user/reservations";
        }
    }
}
