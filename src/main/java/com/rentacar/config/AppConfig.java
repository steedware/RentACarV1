package com.rentacar.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public GeometryFactory geometryFactory() {
        // Using SRID 4326 for WGS84, standard geographic coordinate system
        return new GeometryFactory(new PrecisionModel(), 4326);
    }
}
