package com.example.products.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.products.dto.CreateProdutDto;
import com.example.products.dto.UpdateProcutDto;
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

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(CreateProdutDto productDto, UUID userId) {
        Product product = new Product(productDto, userId);

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

        return productRepository.save(product);
    }

}
