package com.example.shared.common.kafka.dtos.users;

public record KafkaUserCreatedEvent(String userId, String email, String username) {
}
