# --- STAGE 0: Build shared ---
FROM maven:3.9-eclipse-temurin-21 AS shared-build
WORKDIR /app/shared

COPY shared/pom.xml ./pom.xml
COPY shared/src ./src

RUN mvn clean install -DskipTests

# --- STAGE 1: Build product-service ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app/products-service

# Copy product-service pom
COPY products-service/products/pom.xml ./pom.xml

# Copy Maven repo from shared stage
COPY --from=shared-build /root/.m2 /root/.m2

# Cache dependencies
RUN mvn dependency:go-offline -B

# Copy product-service source
COPY products-service/products/src ./src

# Build product-service
RUN mvn clean package -DskipTests

# --- STAGE 2: Run ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy final JAR
COPY --from=build /app/products-service/target/*.jar app.jar

EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
