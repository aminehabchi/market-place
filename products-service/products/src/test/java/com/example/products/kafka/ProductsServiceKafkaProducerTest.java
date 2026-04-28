package com.example.products.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.products.models.Product;
import com.example.shared.common.kafka.dtos.media.KafkaConfirmImageEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductCreatedEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductRemovedEvent;

@SuppressWarnings("null")
class ProductsServiceKafkaProducerTest {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private ProductEvents productEvents;
    private MediaEvents mediaEvents;

    private Product testProduct;
    private UUID imageId;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        productEvents = new ProductEvents(kafkaTemplate);
        mediaEvents = new MediaEvents(kafkaTemplate);

        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
        testProduct.setName("Test Product");
        testProduct.setUserId("user-123");
        testProduct.setPrice(29.99);
        testProduct.setQuantity(10);

        imageId = UUID.randomUUID();
    }

    @Test
    void sendProductCreatedEventPublishesCorrectTopicAndPayload() {
        productEvents.sendCreateEvent(testProduct);

        verify(kafkaTemplate).send(eqTopic("create-product-events"), any(), any(KafkaProductCreatedEvent.class));
    }

    @Test
    void sendProductRemovedEventPublishesCorrectTopicAndPayload() {
        productEvents.sendRemoveEvent(testProduct);

        verify(kafkaTemplate).send(eqTopic("remove-product-events"), any(), any(KafkaProductRemovedEvent.class));
    }

    @Test
    void confirmImageEventPublishesCorrectTopicAndPayload() {
        mediaEvents.confimImageEvent(imageId);

        verify(kafkaTemplate).send(eqTopic("confirm-image-events"), any(), any(KafkaConfirmImageEvent.class));
    }

    @Test
    void deleteImageEventPublishesCorrectTopicAndPayload() {
        mediaEvents.deleteImageEvent(imageId);

        verify(kafkaTemplate).send(eqTopic("delete-image-events"), any(), any(KafkaConfirmImageEvent.class));
    }

    @Test
    void productCreatedEventContainsCorrectData() {
        productEvents.sendCreateEvent(testProduct);

        verify(kafkaTemplate).send(anyString(), any(), any(KafkaProductCreatedEvent.class));
    }

    private String eqTopic(String topic) {
        return org.mockito.ArgumentMatchers.eq(topic);
    }
}