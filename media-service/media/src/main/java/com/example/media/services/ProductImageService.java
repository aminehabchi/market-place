package com.example.media.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.media.models.ProductImage;
import com.example.media.repositories.ProductImageRepository;
import com.example.media.stores.ProductimageContentStore;

@Service
public class ProductImageService {
    private final ProductImageRepository repository;
    private final ProductimageContentStore contentStore;

    public ProductImageService(ProductImageRepository repository,
            ProductimageContentStore contentStore) {
        this.repository = repository;
        this.contentStore = contentStore;
    }

    public ProductImage uploadAvatar(InputStream inputStream, String mimeType) throws Exception {
        // Create metadata
        ProductImage image = new ProductImage();
        image.setMimeType(mimeType);

        // Save metadata first
        image = repository.save(image);

        // Save actual file bytes to filesystem via Spring Content
        contentStore.setContent(image, inputStream);

        repository.save(image);

        return image;
    }

    public void deleteImage(UUID id) throws IOException {

        ProductImage image = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        //  Delete binary content (file)
        contentStore.unsetContent(image);

        // Delete metadata (DB row)
        repository.delete(image);
    }


    public void deleteProductImageByProductId(UUID id){

    }
}
