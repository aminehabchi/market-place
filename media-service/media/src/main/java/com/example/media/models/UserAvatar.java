package com.example.media.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import lombok.Data;

@Data
@Document(collection = "users_avatars")
public class UserAvatar {
    @Id
    private UUID id;

    @ContentId
    private UUID contentId;

    @ContentLength
    private Long contentLength;

    @MimeType
    private String mimeType;

    private String contentPath;
}