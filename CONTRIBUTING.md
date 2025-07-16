# Contributing to RentACar

Thank you for your interest in contributing to the RentACar project! This document provides guidelines and instructions for contributing.

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven
- Git
- Docker and Docker Compose (for local development)

### Setting Up the Development Environment

1. Fork the repository on GitHub
2. Clone your forked repository
   ```
   git clone https://github.com/your-username/RentACar.git
   ```
3. Set up the database using Docker Compose
   ```
   docker-compose up -d
   ```
4. Build the application
   ```
   mvn clean install
   ```
5. Run the application
   ```
   mvn spring-boot:run
   ```

## Development Workflow

1. Create a new branch for your feature or bug fix
   ```
   git checkout -b feature/your-feature-name
   ```
2. Make your changes
3. Write or update tests for your changes
4. Ensure all tests pass
   ```
   mvn test
   ```
5. Commit your changes with a descriptive commit message
   ```
   git commit -m "Add feature: your feature description"
   ```
6. Push your branch to your fork
   ```
   git push origin feature/your-feature-name
   ```
7. Create a pull request from your branch to the main repository's main branch

## Coding Standards

- Follow the Google Java Style Guide
- Write clear, descriptive commit messages
- Include JavaDoc comments for all public methods and classes
- Maintain test coverage for your code

## Testing

- Write unit tests for all new functionality
- Ensure all existing tests pass before submitting a pull request
- Include integration tests where appropriate

## Pull Request Process

1. Update the README.md or documentation with details of changes if applicable
2. Update the version number in pom.xml following semantic versioning
3. The pull request will be merged once it receives approval from a maintainer

## Code of Conduct

- Be respectful and inclusive
- Give credit where it's due
- Focus on the issue, not the person
- Be constructive in feedback

Thank you for contributing to RentACar!
