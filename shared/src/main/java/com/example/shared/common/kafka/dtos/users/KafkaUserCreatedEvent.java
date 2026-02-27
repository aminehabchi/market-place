package com.example.shared.common.kafka.dtos.users;

import java.util.UUID;

public record KafkaUserCreatedEvent(String userId, String username, UUID avatar) {
}
