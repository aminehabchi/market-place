package com.example.products.kafka.dto;

import java.util.UUID;

public record UserUpdatedEvent(UUID userId, String username) {
}
