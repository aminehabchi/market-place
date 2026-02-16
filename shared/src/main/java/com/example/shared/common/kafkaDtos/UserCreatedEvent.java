package com.example.shared.common.kafkaDtos;

public record UserCreatedEvent(UUID userId, String username) {
}
