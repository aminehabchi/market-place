#!/bin/bash

# Exit immediately if any command fails
set -e

echo "Cleaning and compiling the project..."
./mvnw clean compile

echo "Running Spring Boot application..."
./mvnw spring-boot:run