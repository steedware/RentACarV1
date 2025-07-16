package com.rentacar.config;

import com.rentacar.model.User;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.UserRepository;
import com.rentacar.repository.VehicleRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("!test")
public class DataInitializer {

    // Używamy SRID 4326 dla WGS84, standardowego układu współrzędnych geograficznych
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                  VehicleRepository vehicleRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if not exists
            if (!userRepository.existsByEmail("admin@rentacar.com")) {
                User admin = new User();
                admin.setEmail("admin@rentacar.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setPhoneNumber("1234567890");
                admin.setRole(User.Role.ROLE_ADMIN); // Ensure consistent role name
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("Created admin user: admin@rentacar.com / admin");
            }

            // Add some demo vehicles if repository is empty
            if (vehicleRepository.count() == 0) {
                // Create demo vehicles
                List<Vehicle> demoVehicles = Arrays.asList(
                    createVehicle("Toyota", "Corolla", 2020, Vehicle.VehicleType.SEDAN, "Czerwony", 
                                 "WA12345", 5, "Benzyna", new BigDecimal("150.00"), true, 
                                 52.229676, 21.012229),
                    createVehicle("Honda", "Civic", 2021, Vehicle.VehicleType.HATCHBACK, "Niebieski", 
                                 "WA67890", 5, "Diesel", new BigDecimal("160.00"), true, 
                                 52.232618, 21.006874),
                    createVehicle("Ford", "Focus", 2019, Vehicle.VehicleType.COMPACT, "Czarny", 
                                 "WA54321", 5, "Benzyna", new BigDecimal("140.00"), true, 
                                 52.239381, 21.044130)
                );
                
                vehicleRepository.saveAll(demoVehicles);
                System.out.println("Added demo vehicles");
            }
        };
    }
    
    private Vehicle createVehicle(String brand, String model, int year, Vehicle.VehicleType type,
                                String color, String licensePlate, int seats, String fuelType,
                                BigDecimal dailyRate, boolean available, double latitude, double longitude) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setType(type);
        vehicle.setColor(color);
        vehicle.setLicensePlate(licensePlate);
        vehicle.setSeats(seats);
        vehicle.setFuelType(fuelType);
        vehicle.setDailyRate(dailyRate);
        vehicle.setAvailable(available);
        
        // Create and set location point
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        vehicle.setLocation(location);
        vehicle.setLatitude(latitude);
        vehicle.setLongitude(longitude);
        
        return vehicle;
    }
}
