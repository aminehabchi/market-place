package com.example.shared.common.kafkaDtos;

import java.util.UUID;

public record KafkaUserUpdatedEvent(UUID userId, String username) {
}
