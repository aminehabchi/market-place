package com.example.media.stores;

import org.springframework.content.fs.store.FilesystemContentStore;

import java.util.UUID;

import com.example.media.models.ProductImage;

public interface ProductimageContentStore extends FilesystemContentStore<ProductImage, UUID> {
}