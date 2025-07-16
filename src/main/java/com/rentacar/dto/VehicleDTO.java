package com.rentacar.dto;

import com.rentacar.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    private Vehicle.VehicleType type;
    private String color;
    private BigDecimal dailyRate;
    private Boolean available;
    private String fuelType;
    private Integer seats;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private Double rating;
    private Integer ratingCount;
    private Double distance;

    // Constructor from entity
    public VehicleDTO(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.brand = vehicle.getBrand();
        this.model = vehicle.getModel();
        this.year = vehicle.getYear();
        this.licensePlate = vehicle.getLicensePlate();
        this.type = vehicle.getType();
        this.color = vehicle.getColor();
        this.dailyRate = vehicle.getDailyRate();
        this.available = vehicle.isAvailable();
        this.fuelType = vehicle.getFuelType();
        this.seats = vehicle.getSeats();
        this.latitude = vehicle.getLatitude();
        this.longitude = vehicle.getLongitude();
        this.imageUrl = vehicle.getImageUrl();
        this.rating = vehicle.getRating();
        this.ratingCount = vehicle.getRatingCount();
    }

    // Convert to entity
    public Vehicle toEntity() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(this.id);
        vehicle.setBrand(this.brand);
        vehicle.setModel(this.model);
        vehicle.setYear(this.year);
        vehicle.setLicensePlate(this.licensePlate);
        vehicle.setType(this.type);
        vehicle.setColor(this.color);
        vehicle.setDailyRate(this.dailyRate);
        vehicle.setAvailable(this.available);
        vehicle.setFuelType(this.fuelType);
        vehicle.setSeats(this.seats);
        vehicle.setLatitude(this.latitude);
        vehicle.setLongitude(this.longitude);
        vehicle.setImageUrl(this.imageUrl);
        vehicle.setRating(this.rating);
        vehicle.setRatingCount(this.ratingCount);
        
        return vehicle;
    }
}
