package com.example.media.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/products")
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

    @PostMapping("/")
    public ResponseEntity<UUID> uploadImage(
            @RequestBody byte[] fileBytes,
            @RequestHeader("Content-Type") String mimeType, Authentication authentication) throws Exception {

        String userId = (String) authentication.getPrincipal();

        ProductImage avatar = productImageService.uploadAvatar(
                new ByteArrayInputStream(fileBytes),
                mimeType, userId);

        System.out.println("Image uploaded =====> " + avatar.getId());

        return ResponseEntity.ok(avatar.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id, Authentication authentication) throws Exception {
        String userId = (String) authentication.getPrincipal();

        ProductImage image = this.productImageService.getAvatarbyId(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        if (!image.getUserId().equals(userId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not the owner of the image");
        }

        this.productImageService.deleteImage(image);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id) throws Exception {

        Optional<ProductImage> optionalImage = repository.findById(id);

        if (optionalImage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductImage image = optionalImage.get();

        try (InputStream is = contentStore.getContent(image)) {

            byte[] bytes = is.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, image.getMimeType())
                    .contentLength(image.getContentLength())
                    .body(bytes);
        }
    }

}