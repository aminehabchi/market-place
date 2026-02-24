package com.example.media.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.shared.common.kafkaDtos.KafkaproductCreatedEvent;
import com.example.shared.common.kafkaDtos.KafkaproductRemovedEvent;

@Service
public class ProductEvents {
     private final productService productService;

    public productEvents(productService productService) {
        this.productService = productsService;
    }

    @KafkaListener(topics = "create-product-events", groupId = "media-group")
    public void listenCreateproduct(KafkaproductCreatedEvent object) {

        this.productsService.createproduct(object);
    }

    
    @KafkaListener(topics = "remove-product-events", groupId = "media-group")
    public void listenRemoveproduct(KafkaproductRemovedEvent object) {

        this.productsService.deleteProduct(object);
    }
}
