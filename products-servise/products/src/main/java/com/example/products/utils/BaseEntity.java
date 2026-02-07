package com.example.products.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseEntity {
    @Id
    private UUID id = UUID.randomUUID();
    
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
