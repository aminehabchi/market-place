package com.example.products.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.products.models.Product;
import com.example.shared.common.kafka.dtos.products.KafkaProductCreatedEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductRemovedEvent;
import com.example.shared.common.kafka.dtos.media.KafkaConfirmImageEvent;

@SpringBootTest
@SuppressWarnings("null")
class ProductsServiceKafkaProducerTest {
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProductEvents productEvents;

    @Autowired
    private MediaEvents mediaEvents;

    private Product testProduct;
    private UUID imageId;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
        testProduct.setName("Test Product");
        testProduct.setUserId("user-123");
        testProduct.setPrice(29.99);
        testProduct.setQuantity(10);
        
        imageId = UUID.randomUUID();
    }

    @Test
    void testSendProductCreatedEvent() {
        productEvents.sendCreateEvent(testProduct);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), any(), eventCaptor.capture());

        assert "create-product-events".equals(topicCaptor.getValue());
        assert eventCaptor.getValue() instanceof KafkaProductCreatedEvent;
    }

    @Test
    void testSendProductRemovedEvent() {
        productEvents.sendRemoveEvent(testProduct);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), any(), eventCaptor.capture());

        assert "remove-product-events".equals(topicCaptor.getValue());
        assert eventCaptor.getValue() instanceof KafkaProductRemovedEvent;
    }

    @Test
    void testConfirmImageEvent() {
        mediaEvents.confimImageEvent(imageId);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), any(), eventCaptor.capture());

        assert "confirm-image-events".equals(topicCaptor.getValue());
        assert eventCaptor.getValue() instanceof KafkaConfirmImageEvent;
    }

    @Test
    void testDeleteImageEvent() {
        mediaEvents.deleteImageEvent(imageId);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), any(), eventCaptor.capture());

        assert "delete-image-events".equals(topicCaptor.getValue());
        assert eventCaptor.getValue() instanceof KafkaConfirmImageEvent;
    }

    @Test
    void testProductEventContainsCorrectData() {
        productEvents.sendCreateEvent(testProduct);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(anyString(), any(), eventCaptor.capture());

        KafkaProductCreatedEvent event = (KafkaProductCreatedEvent) eventCaptor.getValue();
        assert event.productId().equals(testProduct.getId());
        assert "user-123".equals(event.userId());
    }
}
