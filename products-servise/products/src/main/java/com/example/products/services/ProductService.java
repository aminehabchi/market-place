package com.example.products.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.products.models.Product;
import com.example.products.repositories.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
