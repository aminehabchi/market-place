package com.example.media.controllers;

import com.example.media.models.ProductImage;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.UUID;

import com.example.media.repositories.ProductRepository;
import com.example.media.services.ProductImageSrvice;
import com.example.media.stores.ProductimageContentStore;

@RestController
@RequestMapping("/api/media/products")
public class ProductController {

    private final ProductImageSrvice productImageService;
    private final ProductRepository repository;
    private final ProductimageContentStore contentStore;

    public ProductController(ProductImageSrvice productImageService,
            ProductRepository repository,
            ProductimageContentStore contentStore) {
        this.productImageService = productImageService;
        this.repository = repository;
        this.contentStore = contentStore;
    }

    @PostMapping("")
    public ResponseEntity<UUID> uploadImage(
            @RequestBody byte[] fileBytes,
            @RequestHeader("Content-Type") String mimeType) throws Exception {

        ProductImage avatar = productImageService.uploadAvatar(
                new ByteArrayInputStream(fileBytes),
                mimeType);

        return ResponseEntity.ok(avatar.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteImage(@PathVariable UUID id) throws Exception {
        
        productImageService.deleteImage(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id)
            throws Exception {

        ProductImage image = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        try (InputStream is = contentStore.getContent(image)) {

            byte[] bytes = is.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, image.getMimeType())
                    .contentLength(image.getContentLength())
                    .body(bytes);
        }
    }
}