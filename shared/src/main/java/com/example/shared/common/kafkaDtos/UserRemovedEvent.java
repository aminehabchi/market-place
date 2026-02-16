package com.example.shared.common.kafkaDtos;
import java.util.UUID;

public record UserRemovedEvent(UUID userId) {
}
