package com.example.media.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.media.models.UserAvatar;
import com.example.media.repositories.AvatarRepository;
import com.example.media.services.AvatarService;
import com.example.media.stores.UserAvatarContentStore;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/api/media/users")
public class UserController {

    private final AvatarService avatarService;
    private final AvatarRepository repository;
    private final UserAvatarContentStore contentStore;

    public UserController(AvatarService avatarService,
            AvatarRepository repository,
            UserAvatarContentStore contentStore) {
        this.avatarService = avatarService;
        this.repository = repository;
        this.contentStore = contentStore;
    }

    @PostMapping("/")
    public ResponseEntity<UUID> uploadAvatar(
            @RequestBody byte[] fileBytes,
            @RequestHeader("Content-Type") String mimeType) throws Exception {
        System.out.println("=======================>>>>>>>>>>>>>>>>>>>>>>>> image Uploded");
        UserAvatar avatar = avatarService.uploadAvatar(
                new ByteArrayInputStream(fileBytes),
                mimeType);

        return ResponseEntity.ok(avatar.getId());
    }

    @GetMapping("/{id}")
    @PermitAll
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
