package com.example.media.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.media.models.ProductImage;
import com.example.media.repositories.ProductImageRepository;
import com.example.media.services.ProductImageService;
import com.example.media.stores.ProductimageContentStore;

@RestController
@RequestMapping("/api/media/products")
public class ProductController {

    private final ProductImageService productImageService;
    private final ProductImageRepository repository;
    private final ProductimageContentStore contentStore;

    public ProductController(ProductImageService productImageService,
            ProductImageRepository repository,
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