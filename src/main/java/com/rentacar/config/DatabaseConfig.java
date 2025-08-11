package com.rentacar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.jdbc.DataSourceBuilder;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        System.out.println("=== DATABASE CONFIGURATION ===");
        System.out.println("DATABASE_URL from environment: " +
            (databaseUrl != null ? databaseUrl.substring(0, Math.min(50, databaseUrl.length())) + "..." : "null"));

        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            try {
                // Parse Render database URL
                URI dbUri = new URI(databaseUrl);

                String userInfo = dbUri.getUserInfo();
                if (userInfo == null || !userInfo.contains(":")) {
                    throw new IllegalArgumentException("Invalid DATABASE_URL format - missing credentials");
                }

                String[] credentials = userInfo.split(":");
                String username = credentials[0];
                String password = credentials[1];
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                System.out.println("Parsed JDBC URL: " + jdbcUrl);
                System.out.println("Username: " + username);
                System.out.println("Password length: " + (password != null ? password.length() : 0));

                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                dataSource.setDriverClassName("org.postgresql.Driver");

                // HikariCP settings optimized for Render
                dataSource.setConnectionTimeout(30000);
                dataSource.setIdleTimeout(600000);
                dataSource.setMaxLifetime(1800000);
                dataSource.setMaximumPoolSize(5);
                dataSource.setMinimumIdle(1);
                dataSource.setPoolName("RenderHikariPool");

                System.out.println("Successfully created DataSource for Render PostgreSQL");
                return dataSource;

            } catch (URISyntaxException e) {
                System.err.println("Error parsing DATABASE_URL as URI: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error creating DataSource from DATABASE_URL: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Using default local database configuration");
        // Default configuration for local development
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/rentacar");
        dataSource.setUsername("postgres");
        dataSource.setPassword("admin");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);

        return dataSource;
    }
}
