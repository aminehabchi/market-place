package com.example.media.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.media.models.ProductImage;
import com.example.media.repositories.ProductImageRepository;
import com.example.media.stores.ProductimageContentStore;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository repository;

    @Mock
    private ProductimageContentStore contentStore;

    private ProductImageService service;

    @BeforeEach
    void setUp() {
        service = new ProductImageService(repository, contentStore);
    }

    @Test
    void isImageMimeTypeHandlesNullAndImagePrefix() {
        assertFalse(service.isImageMimeType(null));
        assertTrue(service.isImageMimeType("image/jpeg"));
    }

    @Test
    void deleteImageByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteImagebyId(id));
    }

    @Test
    void confirmImageSavesWhenFound() {
        UUID id = UUID.randomUUID();
        ProductImage image = new ProductImage();
        when(repository.findById(id)).thenReturn(Optional.of(image));

        service.confirmImage(id);

        verify(repository).save(any(ProductImage.class));
    }
}
