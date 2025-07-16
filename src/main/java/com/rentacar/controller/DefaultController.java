package com.rentacar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    // Remove duplicate mappings that conflict with AuthController
    
    // Alternative mapping that doesn't conflict
    @GetMapping("/welcome")
    public String welcome() {
        return "home";
    }
}
