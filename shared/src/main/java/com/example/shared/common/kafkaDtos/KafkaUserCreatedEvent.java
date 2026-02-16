package com.example.shared.common.kafkaDtos;

import java.util.UUID;


public record KafkaUserCreatedEvent(UUID userId, String username) {
}
