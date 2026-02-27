package com.buy01.users.Entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public record User(
        @Id String id,
        String name,
        String email,
        @JsonIgnore String password,
        String role,
        UUID avatarUrl) {
    public User {
        if (role == null) {
            role = "CLIENT";
        }
    }
}
