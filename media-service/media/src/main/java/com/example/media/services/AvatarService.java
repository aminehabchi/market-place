package com.example.media.services;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.media.models.UserAvatar;
import com.example.media.repositories.AvatarRepository;
import com.example.media.stores.UserAvatarContentStore;

@Service
public class AvatarService {

    private final AvatarRepository repository;
    private final UserAvatarContentStore contentStore;

    public AvatarService(AvatarRepository repository,
            UserAvatarContentStore contentStore) {
        this.repository = repository;
        this.contentStore = contentStore;
    }

    // Updated method
    public UserAvatar uploadAvatar(InputStream inputStream, String mimeType) throws Exception {
        // Create metadata
        UserAvatar avatar = new UserAvatar();
        avatar.setId(UUID.randomUUID());
        avatar.setMimeType(mimeType);

        // Save metadata first
        avatar = repository.save(avatar);

        // Save actual file bytes to filesystem via Spring Content
        contentStore.setContent(avatar, inputStream);

        repository.save(avatar);

        return avatar;
    }

    public void deleteAvatarByUserId(String userId) {

    }
}
