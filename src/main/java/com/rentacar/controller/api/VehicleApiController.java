package com.rentacar.controller.api;

import com.rentacar.model.Vehicle;
import com.rentacar.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleApiController {

    private final VehicleService vehicleService;

    @GetMapping("/public/all")
    public ResponseEntity<?> getAllAvailableVehicles() {
        try {
            List<Vehicle> vehicles = vehicleService.getAllAvailableVehicles();
            
            // Filter out vehicles without coordinates
            vehicles = vehicles.stream()
                .filter(v -> v.getLatitude() != null && v.getLongitude() != null)
                .collect(Collectors.toList());
            
            // Convert to simple maps to avoid potential serialization issues
            List<Map<String, Object>> simplifiedVehicles = vehicles.stream()
                .map(this::convertToSimpleMap)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(simplifiedVehicles);
        } catch (Exception e) {
            log.error("Error fetching all vehicles", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/public/nearby")
    public ResponseEntity<?> getNearbyVehicles(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radius,
            @RequestParam(required = false) Vehicle.VehicleType type) {
        
        try {
            List<Vehicle> vehicles = vehicleService.getVehiclesNearLocation(latitude, longitude, radius);
            
            // Apply type filter if provided
            if (type != null) {
                vehicles = vehicles.stream()
                    .filter(v -> v.getType() == type)
                    .collect(Collectors.toList());
            }
            
            // Convert to simple maps to avoid potential serialization issues
            List<Map<String, Object>> simplifiedVehicles = vehicles.stream()
                .map(vehicle -> {
                    Map<String, Object> map = convertToSimpleMap(vehicle);
                    
                    // Calculate and add distance for nearby vehicles
                    if (vehicle.getLatitude() != null && vehicle.getLongitude() != null) {
                        double distance = calculateDistance(
                            latitude, longitude, 
                            vehicle.getLatitude(), vehicle.getLongitude());
                        map.put("distance", distance);
                    }
                    
                    return map;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(simplifiedVehicles);
        } catch (Exception e) {
            log.error("Error fetching nearby vehicles", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // Helper method to calculate distance between two points (Haversine formula)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                 
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    // Helper method to convert Vehicle to a simple Map to avoid serialization issues
    private Map<String, Object> convertToSimpleMap(Vehicle vehicle) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id", vehicle.getId());
        map.put("brand", vehicle.getBrand());
        map.put("model", vehicle.getModel());
        map.put("year", vehicle.getYear());
        map.put("type", vehicle.getType() != null ? vehicle.getType().name() : null);
        map.put("color", vehicle.getColor());
        map.put("licensePlate", vehicle.getLicensePlate());
        map.put("seats", vehicle.getSeats());
        map.put("fuelType", vehicle.getFuelType());
        map.put("dailyRate", vehicle.getDailyRate());
        map.put("available", vehicle.isAvailable());
        map.put("latitude", vehicle.getLatitude());
        map.put("longitude", vehicle.getLongitude());
        map.put("imageUrl", vehicle.getImageUrl());
        map.put("rating", vehicle.getRating());
        map.put("ratingCount", vehicle.getRatingCount());
        
        return map;
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateVehicleLocation(
            @PathVariable Long id,
            @RequestBody Map<String, Double> location) {
        
        Double latitude = location.get("latitude");
        Double longitude = location.get("longitude");
        
        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().body("Latitude and longitude are required");
        }
        
        try {
            vehicleService.updateVehicleLocation(id, latitude, longitude);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating vehicle location", e);
            return ResponseEntity.internalServerError().body("Error updating vehicle location: " + e.getMessage());
        }
    }
}
