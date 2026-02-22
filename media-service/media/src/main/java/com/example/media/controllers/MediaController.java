package com.example.media.controllers;

import com.example.media.models.UserAvatar;
import com.example.media.repositories.UserRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.UUID;

import com.example.media.repositories.UserAvatarContentStore;
import com.example.media.services.UserAvatarService;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final UserAvatarService userAvatarService;
    private final UserRepository repository;
    private final UserAvatarContentStore contentStore;

    public MediaController(UserAvatarService userAvatarService,
            UserRepository repository,
            UserAvatarContentStore contentStore) {
        this.userAvatarService = userAvatarService;
        this.repository = repository;
        this.contentStore = contentStore;
    }

    // =======================
    // UPLOAD AVATAR
    // =======================
    @PostMapping("/avatar")
    public ResponseEntity<UUID> uploadAvatar(
            @RequestBody byte[] fileBytes,
            @RequestHeader("Content-Type") String mimeType) throws Exception {

        UserAvatar avatar = userAvatarService.uploadAvatar(
                new ByteArrayInputStream(fileBytes),
                mimeType);

        return ResponseEntity.ok(avatar.getId());
    }

    // =======================
    // SERVE AVATAR
    // =======================
    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id)
            throws Exception {

        UserAvatar avatar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avatar not found"));

        try (InputStream is = contentStore.getContent(avatar)) {

            byte[] bytes = is.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, avatar.getMimeType())
                    .contentLength(avatar.getContentLength())
                    .body(bytes);
        }
    }
}