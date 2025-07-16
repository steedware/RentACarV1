package com.rentacar.service;

import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;
    
    private VehicleService vehicleService;
    private GeometryFactory geometryFactory;
    private Vehicle testVehicle;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create the GeometryFactory instance
        geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        
        // Match the constructor signature - pass both repository and geometryFactory
        vehicleService = new VehicleService(vehicleRepository, geometryFactory);
        
        // Create test vehicle data
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Corolla");
        testVehicle.setType(Vehicle.VehicleType.SEDAN);
        testVehicle.setAvailable(true);
        testVehicle.setDailyRate(new BigDecimal("100.00"));
        
        Point location = geometryFactory.createPoint(new Coordinate(21.0, 52.0));
        testVehicle.setLocation(location);
        testVehicle.setLatitude(52.0);
        testVehicle.setLongitude(21.0);
        
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
    }

    @Test
    void testGetVehicleById() {
        Optional<Vehicle> result = vehicleService.getVehicleById(1L);
        assertTrue(result.isPresent());
        assertEquals("Toyota", result.get().getBrand());
    }
    
    // Additional tests...
}
