#!/bin/bash
# Start script for Render

echo "Starting RentACar application..."

# Set the active profile to production
export SPRING_PROFILES_ACTIVE=prod

# Start the application
java -jar target/rentacar-0.0.1-SNAPSHOT.jar
