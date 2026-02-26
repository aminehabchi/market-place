package com.example.media.models;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    public User(String id) {
        this.id = id;
    }
}
