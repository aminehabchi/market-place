package com.example.shared.common.kafkaDtos;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public record KafkaUserUpdatedEvent(String userId, String username, String avatarUrl) {
}
