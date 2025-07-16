package com.rentacar.controller;

import com.rentacar.model.User;
import com.rentacar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/auth-status")
    public Map<String, Object> authStatus() {
        Map<String, Object> status = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        status.put("isAuthenticated", auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser"));
        status.put("principal", auth != null ? auth.getPrincipal() : null);
        status.put("username", auth != null ? auth.getName() : null);
        status.put("authorities", auth != null ? 
                auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()) : 
                null);
        
        return status;
    }

    @GetMapping("/reset-admin")
    public String resetAdminPassword() {
        Optional<User> adminUser = userRepository.findByEmail("admin@rentacar.com");
        
        if (adminUser.isPresent()) {
            User admin = adminUser.get();
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEnabled(true);
            userRepository.save(admin);
            return "Admin password reset successfully. You can now login with admin@rentacar.com / admin";
        } else {
            return "Admin user not found";
        }
    }

    @GetMapping("/create-admin")
    public String createAdmin() {
        if (userRepository.findByEmail("admin@rentacar.com").isPresent()) {
            return "Admin already exists. Use /test/reset-admin to reset password.";
        }
        
        User admin = new User();
        admin.setEmail("admin@rentacar.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setPhoneNumber("1234567890");
        admin.setRole(User.Role.ROLE_ADMIN);
        admin.setEnabled(true);
        
        userRepository.save(admin);
        return "Created admin user: admin@rentacar.com / admin";
    }

    @GetMapping("/login-debug")
    public String showLoginDebugInfo() {
        return "<html><body>" +
               "<h2>Login Debug Info</h2>" +
               "<p>This page works, which means your web server is running correctly. Try the login page at <a href='/login'>/login</a></p>" +
               "</body></html>";
    }

    @GetMapping("/form-test")
    public String showFormTest() {
        return "<html><body>" +
               "<h2>Form Test</h2>" +
               "<form action='/test/form-submit' method='post'>" +
               "<input type='text' name='testInput' value='Test value'>" +
               "<button type='submit'>Submit Test</button>" +
               "</form>" +
               "</body></html>";
    }

    @PostMapping("/form-submit")
    public String handleFormSubmit(@RequestParam String testInput) {
        return "<html><body>" +
               "<h2>Form Submission Received!</h2>" +
               "<p>You submitted: " + testInput + "</p>" +
               "<a href='/test/form-test'>Try again</a>" +
               "</body></html>";
    }

    @GetMapping("/check-beans")
    public ResponseEntity<Map<String, String>> checkBeans() {
        Map<String, String> status = new HashMap<>();
        status.put("passwordEncoder", passwordEncoder != null ? "Available" : "Missing");
        status.put("userRepository", userRepository != null ? "Available" : "Missing");
        return ResponseEntity.ok(status);
    }
}
