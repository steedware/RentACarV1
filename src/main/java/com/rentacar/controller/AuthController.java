package com.rentacar.controller;

import com.rentacar.model.User;
import com.rentacar.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email, 
            @RequestParam String phoneNumber,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }
        
        try {
            // Check if email already exists
            if (userService.getUserByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email already in use");
                return "redirect:/register";
            }
            
            // Create user object
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(password); // Will be encoded in the service
            
            // Register the user
            userService.registerUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again.");
            return "redirect:/register";
        }
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        try {
            log.info("Showing login form");
            return "login";
        } catch (Exception e) {
            log.error("Error showing login form", e);
            model.addAttribute("errorMessage", "An error occurred while loading the login page: " + e.getMessage());
            return "error/login-error"; // Use dedicated login error page
        }
    }
    
    @GetMapping("/login-error")
    public String loginError(Model model) {
        log.warn("Login error occurred");
        model.addAttribute("errorMessage", "Invalid credentials or account is disabled");
        return "error/login-error";
    }
}
