package com.example.products.controllers;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    @PermitAll
    public String getProducts() {
        return "Return all products";
    }

    @GetMapping("/{id}")
    public String getProduct(@PathVariable("id") UUID id) {
        return "Get product with id: " + id;
    }

    @PostMapping
    public String createProduct(@RequestBody String body) {
        return "Create product with body: " + body;
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable("id") UUID id, Authentication authentication) {
        return "Delete product with id: " + id;
    }

    @PutMapping("/{id}")
    public String updateProduct(
            @PathVariable("id") UUID id,
            @Valid @RequestBody String body,
            Authentication authentication) {
        return "Update product with id: " + id + " and body: " + body;
    }



}
