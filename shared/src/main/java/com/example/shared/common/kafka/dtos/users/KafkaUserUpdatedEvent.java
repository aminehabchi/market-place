package com.example.shared.common.kafka.dtos.users;

import java.util.UUID;

public record KafkaUserUpdatedEvent(String userId, String username) {
}
