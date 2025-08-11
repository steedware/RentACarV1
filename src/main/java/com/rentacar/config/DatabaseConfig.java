package com.rentacar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.jdbc.DataSourceBuilder;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.core.annotation.Order;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Order(1) // Ładuj jako pierwszy
public class DatabaseConfig {

    public DatabaseConfig() {
        System.out.println(">>> DatabaseConfig CONSTRUCTOR CALLED <<<");
        String envUrl = System.getenv("DATABASE_URL");
        System.out.println(">>> Constructor DATABASE_URL: " +
            (envUrl != null ? envUrl.substring(0, Math.min(30, envUrl.length())) + "..." : "NULL"));
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        System.out.println(">>> DATASOURCE BEAN CREATION STARTED <<<");

        String databaseUrl = System.getenv("DATABASE_URL");

        System.out.println("=== DATABASE CONFIGURATION ===");
        System.out.println("DATABASE_URL from environment: " +
            (databaseUrl != null ? databaseUrl.substring(0, Math.min(50, databaseUrl.length())) + "..." : "null"));

        // Wypisz wszystkie zmienne środowiskowe związane z bazą danych
        System.out.println("All DB-related environment variables:");
        System.getenv().entrySet().stream()
            .filter(entry -> entry.getKey().contains("DATABASE") || entry.getKey().contains("POSTGRES"))
            .forEach(entry -> System.out.println("  " + entry.getKey() + " = " +
                (entry.getValue().length() > 50 ? entry.getValue().substring(0, 50) + "..." : entry.getValue())));

        // Obsługa zarówno postgresql:// jak i jdbc:postgresql://
        if (databaseUrl != null && (databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("jdbc:postgresql://"))) {
            try {
                String urlToParse = databaseUrl;

                // Jeśli URL już ma jdbc: prefix, usuń go do parsowania
                if (urlToParse.startsWith("jdbc:")) {
                    urlToParse = urlToParse.substring(5); // usuń "jdbc:"
                }

                System.out.println("URL to parse: " + urlToParse);

                // Parse Render database URL
                URI dbUri = new URI(urlToParse);

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
                System.out.println(">>> DATASOURCE BEAN CREATION COMPLETED (RENDER) <<<");
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

        System.out.println(">>> DATASOURCE BEAN CREATION COMPLETED (LOCAL) <<<");
        return dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println(">>> APPLICATION READY - DATABASE CONFIG LOADED <<<");
    }
}
