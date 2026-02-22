package com.example.media.models;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private UUID id;
}
