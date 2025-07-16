package com.rentacar.controller;

import com.rentacar.model.Vehicle;
import com.rentacar.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VehicleService vehicleService;

    @GetMapping("/")
    public String home(Model model) {
        List<Vehicle> featuredVehicles = vehicleService.getAllAvailableVehicles();
        model.addAttribute("featuredVehicles", featuredVehicles);
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
