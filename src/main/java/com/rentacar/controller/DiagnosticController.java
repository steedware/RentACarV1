package com.rentacar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ok");
        status.put("timestamp", System.currentTimeMillis());
        status.put("javaVersion", System.getProperty("java.version"));
        status.put("memory", Runtime.getRuntime().freeMemory() + "/" + Runtime.getRuntime().totalMemory());
        return status;
    }
}
