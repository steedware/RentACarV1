package com.rentacar.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
@Profile("test")
public class TestConfig {

    // Provide necessary beans for the test environment
    @Bean
    public GeometryFactory geometryFactory() {
        // Using SRID 4326 for WGS84, standard geographic coordinate system
        return new GeometryFactory(new PrecisionModel(), 4326);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // Add other beans needed for testing as required
}
