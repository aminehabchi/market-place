package com.example.products.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.products.dto.CreateProdutDto;
import com.example.products.dto.UpdateProcutDto;
import com.example.products.kafka.ProductEvents;
import com.example.products.kafka.MediaEvents;
import com.example.products.models.Product;
import com.example.products.repositories.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEvents productEvents;
    private final MediaEvents mediaEvents;

    public ProductService(ProductRepository productRepository,
            ProductEvents productEvents,
            MediaEvents mediaEvents) {
        this.productRepository = productRepository;
        this.productEvents = productEvents;
        this.mediaEvents = mediaEvents;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Product> getMyProducts(String userId) {
        return productRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product createProduct(CreateProdutDto productDto, String userId) {
        Product product = new Product(productDto, userId);

        // 1 Save first (source of truth)
        Product saved = productRepository.save(product);

        // 2 Then emit events
        productEvents.sendCreateEvent(saved);
        mediaEvents.confimImageEvent(saved.getImage());

        return saved;
    }

    public void deleteProduct(UUID id) {
        Product product = getProductById(id);

        productRepository.deleteById(id);
        mediaEvents.deleteImageEvent(product.getImage());
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

        if (productDto.getImage() != null &&
                !productDto.getImage().equals(product.getImage())) {

            // confirm new image first
            mediaEvents.confimImageEvent(productDto.getImage());

            UUID oldImage = product.getImage();
            product.setImage(productDto.getImage());

            Product saved = productRepository.save(product);

            // delete old image after success
            mediaEvents.deleteImageEvent(oldImage);

            return saved;
        }

        return productRepository.save(product);
    }
}