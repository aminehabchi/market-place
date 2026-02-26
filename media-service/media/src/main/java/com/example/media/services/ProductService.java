package com.example.media.services;

import org.springframework.stereotype.Service;

import com.example.media.models.Product;
import com.example.media.repositories.ProductRepository;
import com.example.shared.common.kafka.dtos.products.KafkaProductCreatedEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductRemovedEvent;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(KafkaProductCreatedEvent object) {
        Product p = new Product(object.id(), object.userId());
        this.productRepository.save(p);
    }

    public void deleteProduct(KafkaProductRemovedEvent object) {
        this.productRepository.deleteById(object.id());
    }
}
