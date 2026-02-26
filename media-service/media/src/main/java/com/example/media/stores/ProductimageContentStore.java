package com.example.media.stores;

import org.springframework.content.fs.store.FilesystemContentStore;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.media.models.ProductImage;

@Repository
public interface ProductimageContentStore extends FilesystemContentStore<ProductImage, UUID> {
}