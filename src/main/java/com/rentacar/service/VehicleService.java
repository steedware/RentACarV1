package com.rentacar.service;

import com.rentacar.model.Reservation;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final GeometryFactory geometryFactory;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, GeometryFactory geometryFactory) {
        this.vehicleRepository = vehicleRepository;
        this.geometryFactory = geometryFactory;
    }
    
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    public List<Vehicle> getAllAvailableVehicles() {
        return vehicleRepository.findByAvailableTrue();
    }
    
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }
    
    public List<Vehicle> getVehiclesByType(Vehicle.VehicleType type) {
        // Changed to use findByType method
        return vehicleRepository.findByType(type);
    }
    
    public List<Vehicle> getAvailableVehiclesInTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        // Added the required ReservationStatus parameter
        return vehicleRepository.findAvailableInTimeRange(startDate, endDate, Reservation.ReservationStatus.CANCELED);
    }
    
    public List<Vehicle> getVehiclesNearLocation(double latitude, double longitude, double radius) {
        // Use the manual distance calculation approach
        return vehicleRepository.findAll().stream()
                .filter(vehicle -> {
                    if (vehicle.getLocation() == null) return false;
                    
                    double distance = calculateDistance(
                            latitude, longitude,
                            vehicle.getLatitude(), vehicle.getLongitude());
                    
                    return distance <= radius;
                })
                .toList();
    }
    
    // Helper method to calculate distance between two coordinates in km (using Haversine formula)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if (lat2 == 0 && lon2 == 0) return Double.MAX_VALUE;
        
        final int R = 6371; // Earth's radius in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    @Transactional
    public Vehicle addVehicle(Vehicle vehicle) {
        // Create point from latitude and longitude
        if (vehicle.getLatitude() != null && vehicle.getLongitude() != null) {
            Point location = geometryFactory.createPoint(
                new Coordinate(vehicle.getLongitude(), vehicle.getLatitude()));
            vehicle.setLocation(location);
        }
        
        return vehicleRepository.save(vehicle);
    }
    
    @Transactional
    public Vehicle updateVehicle(Vehicle vehicle) {
        // Fetch existing vehicle to preserve non-form fields if needed
        Vehicle existingVehicle = vehicleRepository.findById(vehicle.getId())
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        
        // Update the point geometry based on lat/long
        if (vehicle.getLatitude() != null && vehicle.getLongitude() != null) {
            Point location = geometryFactory.createPoint(
                new Coordinate(vehicle.getLongitude(), vehicle.getLatitude()));
            vehicle.setLocation(location);
        } else if (existingVehicle.getLocation() != null) {
            // Keep the existing location if new coordinates aren't provided
            vehicle.setLocation(existingVehicle.getLocation());
        }
        
        // If image wasn't updated, preserve the existing imageUrl
        if (vehicle.getImageUrl() == null || vehicle.getImageUrl().isEmpty()) {
            vehicle.setImageUrl(existingVehicle.getImageUrl());
        }
        
        return vehicleRepository.save(vehicle);
    }
    
    @Transactional
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
    
    @Transactional
    public Vehicle updateVehicleLocation(Long vehicleId, Double latitude, Double longitude) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + vehicleId));
        
        // Update coordinates
        vehicle.setLatitude(latitude);
        vehicle.setLongitude(longitude);
        
        // Update PostGIS Point
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        vehicle.setLocation(location);
        
        return vehicleRepository.save(vehicle);
    }
}
