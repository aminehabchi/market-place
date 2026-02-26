package com.example.media.services;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.media.models.ProductImage;
import com.example.media.repositories.ProductImageRepository;
import com.example.media.stores.ProductimageContentStore;
import com.example.shared.common.types.ImageStatus;

@Service
public class ProductImageService {

    private final ProductImageRepository repository;
    private final ProductimageContentStore contentStore;

    public ProductImageService(ProductImageRepository repository,
                               ProductimageContentStore contentStore) {
        this.repository = repository;
        this.contentStore = contentStore;
    }

    @Transactional
    public ProductImage uploadAvatar(InputStream inputStream, String mimeType) {
        ProductImage image = new ProductImage();
        image.setMimeType(mimeType);

        // Save metadata first
        image = repository.save(image);
        // Save actual file bytes
        contentStore.setContent(image, inputStream);
        // Update entity if content store modifies it
        image = repository.save(image);
        return image;
    }

    @Transactional
    public void deleteImage(UUID id) {
        ProductImage image = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found: " + id));

        // Delete file content
        contentStore.unsetContent(image);
        // Delete metadata
        repository.delete(image);
    }

    @Transactional
    public void deleteProductImageByProductId(UUID productId) {
        List<ProductImage> images = repository.findByProductId(productId);

        for (ProductImage img : images) {
            contentStore.unsetContent(img);
            repository.delete(img);
        }
    }

    @Transactional
    public void confirmImage(UUID id) {
        repository.findById(id).ifPresent(img -> {
            img.setStatus(ImageStatus.LINKED);
            repository.save(img);
        });
    }
}