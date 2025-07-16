package com.rentacar.controller;

import com.rentacar.model.Vehicle;
import com.rentacar.service.ReservationService;
import com.rentacar.service.UserService;
import com.rentacar.service.VehicleService;
import com.rentacar.util.FileUploadUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VehicleService vehicleService;
    private final UserService userService;
    private final ReservationService reservationService;
    
    private static final String VEHICLE_UPLOAD_DIR = "src/main/resources/static/images/vehicles";

    @PostConstruct
    public void init() {
        // Ensure the upload directory exists
        try {
            Path uploadPath = Paths.get(VEHICLE_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created vehicle upload directory: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to create vehicle upload directory: " + e.getMessage());
        }
    }

    @GetMapping
    public String adminDashboard(Model model) {
        long vehicleCount = vehicleService.getAllVehicles().size();
        long userCount = userService.getAllUsers().size();
        long reservationCount = reservationService.getAllReservations().size();
        
        model.addAttribute("vehicleCount", vehicleCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("reservationCount", reservationCount);
        
        return "admin/dashboard";
    }

    // Vehicle Management
    @GetMapping("/vehicles")
    public String manageVehicles(Model model) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "admin/vehicles/list";
    }

    @GetMapping("/vehicles/add")
    public String addVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("vehicleTypes", Vehicle.VehicleType.values());
        return "admin/vehicles/add";
    }

    @PostMapping("/vehicles/add")
    public String addVehicle(
            @ModelAttribute Vehicle vehicle,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    // Use the FileUploadUtil to save the file
                    String fileName = FileUploadUtil.saveFile(VEHICLE_UPLOAD_DIR, imageFile);
                    
                    // Set the image URL for the vehicle (relative path for web access)
                    vehicle.setImageUrl("/images/vehicles/" + fileName);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
                    return "redirect:/admin/vehicles/add";
                }
            }
            
            vehicleService.addVehicle(vehicle);
            redirectAttributes.addFlashAttribute("message", "Vehicle added successfully");
            return "redirect:/admin/vehicles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding vehicle: " + e.getMessage());
            return "redirect:/admin/vehicles/add";
        }
    }

    @GetMapping("/vehicles/edit/{id}")
    public String editVehicleForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id: " + id));
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("vehicleTypes", Vehicle.VehicleType.values());
        return "admin/vehicles/edit";
    }

    @PostMapping("/vehicles/update")
    public String updateVehicle(
            @ModelAttribute Vehicle vehicle,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Use the FileUploadUtil to save the file
                String fileName = FileUploadUtil.saveFile(VEHICLE_UPLOAD_DIR, imageFile);
                
                // Set the image URL for the vehicle (relative path for web access)
                vehicle.setImageUrl("/images/vehicles/" + fileName);
                
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
                return "redirect:/admin/vehicles/edit/" + vehicle.getId();
            }
        }
        
        vehicleService.updateVehicle(vehicle);
        redirectAttributes.addFlashAttribute("message", "Vehicle updated successfully");
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.deleteVehicle(id);
        redirectAttributes.addFlashAttribute("message", "Vehicle deleted successfully");
        return "redirect:/admin/vehicles";
    }

    // User Management - REDIRECT to the new AdminUserController path
    @GetMapping("/users")
    public String redirectToUserManagement() {
        return "redirect:/admin/user-management";
    }

    // Remove Reservation Management methods to avoid conflicts with AdminReservationController
}
