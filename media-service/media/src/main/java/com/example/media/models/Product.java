package com.example.media.models;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "products")
public class Product {

    @Id
    private UUID id;

    @Field("user_id")
    private String userId;

    public Product(UUID id, String userId) {
        this.id = id;
        this.userId = userId;
    }
}
