package com.rentacar.controller;

import com.rentacar.model.Vehicle;
import com.rentacar.service.VehicleService;
import com.rentacar.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private static final String VEHICLE_UPLOAD_DIR = "src/main/resources/static/images/vehicles";

    @GetMapping
    public String getAllVehicles(Model model) {
        List<Vehicle> vehicles = vehicleService.getAllAvailableVehicles();
        model.addAttribute("vehicles", vehicles);
        return "vehicles/list";
    }

    @GetMapping("/search")
    public String searchVehicles(
            @RequestParam(required = false) Vehicle.VehicleType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "10") Double radius,
            Model model) {
        
        List<Vehicle> vehicles;
        
        if (latitude != null && longitude != null) {
            vehicles = vehicleService.getVehiclesNearLocation(latitude, longitude, radius);
        } else if (type != null) {
            vehicles = vehicleService.getVehiclesByType(type);
        } else if (startDate != null && endDate != null) {
            vehicles = vehicleService.getAvailableVehiclesInTimeRange(startDate, endDate);
        } else {
            vehicles = vehicleService.getAllAvailableVehicles();
        }
        
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "vehicles/search-results";
    }

    @GetMapping("/map")
    public String viewMap(Model model) {
        try {
            // Get all vehicles but keep data simple - avoid complex serialization
            List<Vehicle> vehicles = vehicleService.getAllAvailableVehicles();
            
            // Log what we're getting
            System.out.println("Loading vehicle map with " + vehicles.size() + " vehicles");
            
            // Add the simplified list to model
            model.addAttribute("vehicles", vehicles);
            
            // Use a simplified view name to avoid potential path issues
            return "vehicles/map";
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error loading vehicle map: " + e.getMessage());
            e.printStackTrace();
            
            // Add error to model
            model.addAttribute("error", "Error loading vehicle map: " + e.getMessage());
            
            // Return error view
            return "error/error";
        }
    }

    @GetMapping("/{id}")
    public String getVehicleDetails(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id: " + id));
        model.addAttribute("vehicle", vehicle);
        return "vehicles/details";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id: " + id));
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("vehicleTypes", Vehicle.VehicleType.values());
        return "vehicles/update";
    }

    @PostMapping("/update")
    public String updateVehicle(
            @ModelAttribute Vehicle vehicle,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Handle image upload if a new image is provided
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String fileName = FileUploadUtil.saveFile(VEHICLE_UPLOAD_DIR, imageFile);
                    vehicle.setImageUrl("/images/vehicles/" + fileName);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
                    return "redirect:/vehicles/update/" + vehicle.getId();
                }
            }
            
            // Update the vehicle in the database
            vehicleService.updateVehicle(vehicle);
            redirectAttributes.addFlashAttribute("success", "Vehicle updated successfully");
            return "redirect:/vehicles/" + vehicle.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating vehicle: " + e.getMessage());
            return "redirect:/vehicles/update/" + vehicle.getId();
        }
    }
}
