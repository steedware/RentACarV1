package com.rentacar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class TestAuthController {

    @GetMapping("/status")
    public Map<String, Object> getAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> result = new HashMap<>();
        result.put("isAuthenticated", auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser"));
        result.put("username", auth != null ? auth.getName() : null);
        result.put("authorities", auth != null ? auth.getAuthorities() : null);
        
        return result;
    }
}
