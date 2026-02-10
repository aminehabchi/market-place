# Use official Maven + JDK image
FROM maven:3.9.4-eclipse-temurin-25

# Set working directory
WORKDIR /app

# Copy only the pom.xml first to cache dependencies
COPY pom.xml ./

# Download dependencies (this will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Expose default Spring Boot port
EXPOSE 8080

# Run the application
CMD ["mvn", "spring-boot:run"]
