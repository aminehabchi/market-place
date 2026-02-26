package com.example.shared.common.kafka.dtos.users;

import org.springframework.web.multipart.MultipartFile;

public record KafkaUserCreatedEvent(String userId, String email, String username, MultipartFile avatar) {
}
