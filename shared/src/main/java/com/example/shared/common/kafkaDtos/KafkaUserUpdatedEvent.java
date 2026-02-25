package com.example.shared.common.kafkaDtos;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public record KafkaUserUpdatedEvent(UUID userId, String username, MultipartFile avatar) {
}
