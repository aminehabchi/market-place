package com.example.shared.common.kafkaDtos;
import java.util.UUID;

public record UserUpdatedEvent(UUID userId, String username) {
}
