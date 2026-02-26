package com.example.media.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.media.models.ProductImage;

@Repository
public interface ProductImageRepository extends MongoRepository<ProductImage, UUID> {

    List<ProductImage> findByProductId(UUID id);
}
