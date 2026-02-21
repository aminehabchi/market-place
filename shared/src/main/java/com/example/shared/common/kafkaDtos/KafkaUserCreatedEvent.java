package com.example.shared.common.kafkaDtos;

public record KafkaUserCreatedEvent(String userId, String email, String username) {
}
