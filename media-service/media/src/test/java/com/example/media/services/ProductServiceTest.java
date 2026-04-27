package com.example.media.services;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.media.repositories.ProductRepository;
import com.example.shared.common.kafka.dtos.products.KafkaProductCreatedEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductRemovedEvent;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void createProductSavesEntity() {
        UUID productId = UUID.randomUUID();

        productService.createProduct(new KafkaProductCreatedEvent(productId, "u-1"));

        verify(productRepository).save(org.mockito.ArgumentMatchers.any(com.example.media.models.Product.class));
    }

    @Test
    void deleteProductDeletesById() {
        UUID productId = UUID.randomUUID();

        productService.deleteProduct(new KafkaProductRemovedEvent(productId));

        verify(productRepository).deleteById(productId);
    }
}
