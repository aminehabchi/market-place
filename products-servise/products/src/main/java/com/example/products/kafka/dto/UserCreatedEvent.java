package com.example.products.kafka.dto;

import java.util.UUID;

public record UserCreatedEvent(UUID userId, String username) {
}
