package com.example.shared.common.kafkaDtos;

import org.springframework.web.multipart.MultipartFile;

public record KafkaUserCreatedEvent(String userId, String email, String username, String avatar) {
}
