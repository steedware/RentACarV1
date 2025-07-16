package com.rentacar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Profile("!test") // Don't run this during tests
public class PostGISInitializer implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(PostGISInitializer.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS postgis");
            String postgisVersion = jdbcTemplate.queryForObject("SELECT postgis_full_version()", String.class);
            logger.info("PostGIS initialized successfully: " + postgisVersion);
        } catch (Exception e) {
            logger.severe("Failed to initialize PostGIS: " + e.getMessage());
            // Don't throw the exception to allow application to continue
            // throw e;
        }
    }
}
