package com.example.media.models;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

import lombok.Data;

@Data
@Document(collection = "products_images")
public class ProductImage {
    @Id
    private UUID id = UUID.randomUUID();

    @Field("user_id")
    private UUID userId;

    @Field("product_id")
    private UUID productId;

    @ContentId
    private UUID contentId;

    @ContentLength
    private Long contentLength;

    @MimeType
    private String mimeType;

    private String contentPath;
}