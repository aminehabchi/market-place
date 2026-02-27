package com.example.products.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.products.dto.CreateProdutDto;
import com.example.products.dto.UpdateProcutDto;
import com.example.products.kafka.ProductEvents;
import com.example.products.models.Product;
import com.example.products.repositories.ProductRepository;

import com.example.products.kafka.MediaEvents;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductEvents productEvents;
    private final MediaEvents mediaEvents;

    public ProductService(ProductRepository productRepository, ProductEvents productEvents, MediaEvents mediaEvents) {
        this.productRepository = productRepository;
        this.productEvents = productEvents;
        this.mediaEvents = mediaEvents;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(CreateProdutDto productDto, String userId) {
        Product product = new Product(productDto, userId);
        this.productEvents.sendCreateEvent(product);
        this.mediaEvents.confimImageEvent(product.getImage());

        return productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    public Product updateProduct(Product product, UpdateProcutDto productDto) {
        if (productDto.getName() != null && !productDto.getName().isBlank()) {
            product.setName(productDto.getName());
        }

        if (productDto.getDescription() != null && !productDto.getDescription().isBlank()) {
            product.setDescription(productDto.getDescription());
        }

        if (productDto.getPrice() != null && productDto.getPrice() > 0) {
            product.setPrice(productDto.getPrice());
        }

        if (productDto.getImage() != null && !productDto.getImage().equals(product.getImage())) {
            product.setImage(productDto.getImage());
            this.mediaEvents.confimImageEvent(productDto.getImage());
            this.mediaEvents.deleteImageEvent(product.getImage());
        }

        return productRepository.save(product);
    }

}
