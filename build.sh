#!/bin/bash
# Build script for Render

echo "Starting build process..."

# Install dependencies and build the application
mvn clean package -DskipTests

echo "Build completed successfully!"
