package com.example.media.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.media.services.ProductImageService;
import com.example.media.services.ProductService;
import com.example.shared.common.kafka.dtos.products.KafkaProductCreatedEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductRemovedEvent;

@Service
public class ProductEvents {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductEvents(ProductService productService, ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }

    @KafkaListener(topics = "create-product-events", groupId = "media-group")
    public void listenCreateproduct(KafkaProductCreatedEvent object) {
        this.productService.createProduct(object);
    }

    @KafkaListener(topics = "remove-product-events", groupId = "media-group")
    public void listenRemoveproduct(KafkaProductRemovedEvent object) {

        this.productImageService.deleteProductImageByProductId(object.id());

        this.productService.deleteProduct(object);
    }
}
