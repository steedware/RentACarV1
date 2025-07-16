package com.rentacar.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle")
@Data
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    private String color;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    private int seats;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "daily_rate", nullable = false)
    private BigDecimal dailyRate;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "image_url")
    private String imageUrl;

    // Geospatial data
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    // For easier front-end integration
    private Double latitude;
    private Double longitude;

    // Rating information
    private Double rating;
    
    @Column(name = "rating_count")
    private Integer ratingCount;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public enum VehicleType {
        SEDAN, SUV, HATCHBACK, COMPACT, ECONOMY
    }
}
