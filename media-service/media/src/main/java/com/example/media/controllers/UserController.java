package com.example.media.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.media.models.UserAvatar;
import com.example.media.services.AvatarService;
import com.example.media.stores.UserAvatarContentStore;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/users")
public class UserController {

    private final AvatarService avatarService;

    private final UserAvatarContentStore contentStore;

    public UserController(AvatarService avatarService, UserAvatarContentStore contentStore) {
        this.avatarService = avatarService;
        this.contentStore = contentStore;
    }

    @PostMapping("/")
    public ResponseEntity<?> uploadAvatar(
            @RequestBody byte[] fileBytes,
            @RequestHeader("Content-Type") String mimeType, Authentication authentication) throws Exception {

        if (!this.avatarService.isImageMimeType(mimeType)) {
            return ResponseEntity
                    .badRequest()
                    .body("File must be an image");
        }

        String userId = (String) authentication.getPrincipal();

        UserAvatar avatar = avatarService.uploadAvatar(
                new ByteArrayInputStream(fileBytes),
                mimeType, userId);

        return ResponseEntity.ok(avatar.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletAvatar(@PathVariable UUID id, Authentication authentication) throws Exception {
        String userId = (String) authentication.getPrincipal();

        UserAvatar avatar = this.avatarService.getAvatarbyId(id);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        if (!avatar.getUserId().equals(userId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not the owner of the image");
        }

        this.avatarService.deleteAvatar(avatar);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id)
            throws Exception {

        UserAvatar avatar = this.avatarService.getAvatarbyId(id);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        try (InputStream is = contentStore.getContent(avatar)) {

            byte[] bytes = is.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, avatar.getMimeType())
                    .contentLength(avatar.getContentLength())
                    .body(bytes);
        }
    }
}
