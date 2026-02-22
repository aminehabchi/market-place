package com.example.media.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

import lombok.Data;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private UUID id;
}