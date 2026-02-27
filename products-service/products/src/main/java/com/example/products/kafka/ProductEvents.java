package com.example.products.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.products.models.Product;

import com.example.shared.common.kafka.dtos.products.*;

@Service
public class ProductEvents {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductEvents(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCreateEvent(Product p) {
        KafkaProductCreatedEvent event = new KafkaProductCreatedEvent(p.getId(), p.getUserId());
        kafkaTemplate.send("create-product-events", null, event);
    }

    public void sendRemoveEvent(Product p) {
        KafkaProductRemovedEvent event = new KafkaProductRemovedEvent(p.getId());
        kafkaTemplate.send("remove-product-events", null, event);
    }
}
