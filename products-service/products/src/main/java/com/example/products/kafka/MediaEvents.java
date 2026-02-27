package com.example.products.kafka;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.shared.common.kafka.dtos.media.KafkaConfirmImageEvent;

@Service
public class MediaEvents {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MediaEvents(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void confimImageEvent(UUID image) {
        KafkaConfirmImageEvent event = new KafkaConfirmImageEvent(image);
        kafkaTemplate.send("confirm-image-events", null, event);
    }

    public void deleteImageEvent(UUID image) {
        KafkaConfirmImageEvent event = new KafkaConfirmImageEvent(image);
        kafkaTemplate.send("delete-image-events", null, event);
    }
}
