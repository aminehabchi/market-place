package com.buy01.media.repositories;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.buy01.media.models.Product;


public interface ProductRepository extends MongoRepository<Product, UUID> {

}