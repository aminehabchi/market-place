package com.example.media.repositories;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.media.models.ProductImage;


@Repository
public interface ProductRepository extends MongoRepository<ProductImage, UUID> {

}