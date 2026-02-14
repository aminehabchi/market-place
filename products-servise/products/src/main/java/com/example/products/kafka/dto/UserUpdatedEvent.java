package com.example.products.kafka.dto;

public record UserUpdatedEvent(UUID userId, String username) {
}
