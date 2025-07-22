# RentACar - Car Rental System with Geolocation

## Overview

RentACar is a comprehensive web-based car rental system that allows users to find, book, and rent vehicles with real-time geolocation tracking features. The application provides an intuitive interface for customers to search for vehicles by location, type, and availability, and offers a robust admin panel for managing the fleet.

## Features

### Customer Features
- User registration and authentication
- Search vehicles by location, type, and availability
- View vehicles on interactive maps
- Make reservations with online payments
- Track rental history and status
- Rate and review vehicle rentals

### Admin Features
- Fleet management (add, edit, delete vehicles)
- User management
- Reservation supervision
- Analytics and reporting
- Fleet geolocation tracking

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.1.5
- Spring Security
- Spring Data JPA
- Hibernate Spatial for geolocation

### Frontend
- Thymeleaf templates
- Bootstrap 5
- Leaflet.js for maps
- JavaScript

### Database
- PostgreSQL with PostGIS extension for geographic functions

### External Integrations
- Stripe for payment processing
- OpenStreetMap for geolocation

## Installation Guide

### Prerequisites
- Java 17 or newer
- Maven
- PostgreSQL with PostGIS extension
- Stripe account for payment processing

### Database Setup
1. Create a PostgreSQL database named `rentacar`
2. Enable PostGIS extension by running: `CREATE EXTENSION postgis;`

### Application Setup
1. Clone the repository
2. Configure the database connection in `application.properties`
3. Set Stripe API keys in `application.properties`
4. Build the project with Maven: `mvn clean install`
5. Run the application: `mvn spring-boot:run`
6. Access the app at: `http://localhost:8080`

### Default Admin Account
On the first run, an admin account is created with the following credentials:
- Email: admin@rentacar.com
- Password: admin

(Remember to change these credentials in production)

## Project Structure

- `src/main/java/com/rentacar/controller/` - Web controllers
- `src/main/java/com/rentacar/model/` - Entity models
- `src/main/java/com/rentacar/repository/` - Data repositories
- `src/main/java/com/rentacar/service/` - Business logic services
- `src/main/java/com/rentacar/config/` - Configuration classes
- `src/main/resources/templates/` - Thymeleaf templates
- `src/main/resources/static/` - Static resources (CSS, JS, images)

## API Documentation

The application provides several RESTful endpoints for vehicle geolocation and payments:

- `GET /api/vehicles/public/all` - Get all available vehicles
- `GET /api/vehicles/public/nearby` - Get vehicles near specific coordinates
- `PUT /api/vehicles/{id}/location` - Update vehicle location

## License

This project is licensed under the MIT License – see the LICENSE file for details.
