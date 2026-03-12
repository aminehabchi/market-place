# E-Commerce Microservices Platform
A distributed e-commerce ecosystem built with Spring Boot, Angular, and a microservices architecture. This project leverages Eureka for service discovery, Spring Cloud Gateway for routing, and Kafka/Redis for messaging and caching.

# Architecture Overview
The system is composed of several specialized services:

- **frontend**: Angular-based web interface.

- **gateway**: Central entry point using Spring Cloud Gateway (includes rate limiting).

- **eureka-server**: Service registry and discovery.

- **users-service**: Manages user profiles, authentication, and authorization.

- **products-service**: Handles product catalog and inventory.

- **media-service**: Manages image uploads and static assets.

- **shared**: Common library containing shared DTOs and utilities.


# Technology Stack

| Component            | Technology                      |
| :------------------- | :------------------------------ |
| **Frontend**         | Angular 21.1.3                     |
| **API Gateway**      | Spring Cloud Gateway            |
| **Registry**         | Netflix Eureka Server           |
| **Databases**        | MongoDB (Per-service instances) |
| **File Storage**     | Spring Content FS               |
| **Caching**          | Redis                           |
| **Messaging**        | Apache Kafka                    |
| **Security**         | JWT, OpenSSL (MTLS/HTTPS)       |
| **Containerization** | Docker, Docker Compose          |

# Launching
The project includes orchestration scripts to manage the complex microservice lifecycle:

 - Start All Services: ./scripts/run-all.sh

 - Stop All Services: ./scripts/stop.sh

 - Full Cleanup: ./scripts/remove-all.sh