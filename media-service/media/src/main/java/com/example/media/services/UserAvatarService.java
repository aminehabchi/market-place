package com.example.media.services;

import com.example.media.models.UserAvatar;
import com.example.media.repositories.*;


import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UserAvatarService {

    private final UserRepository repository;
    private final UserAvatarContentStore contentStore;

    public UserAvatarService(UserRepository repository,
                             UserAvatarContentStore contentStore) {
        this.repository = repository;
        this.contentStore = contentStore;
    }

    // ✅ Updated method
    public UserAvatar uploadAvatar(InputStream inputStream, String mimeType) throws Exception {
        // 1️⃣ Create metadata
        UserAvatar avatar = new UserAvatar();
        avatar.setId(UUID.randomUUID());
        avatar.setMimeType(mimeType);

        // 2️⃣ Save metadata first
        avatar = repository.save(avatar);

        // 3️⃣ Save actual file bytes to filesystem via Spring Content
        contentStore.setContent(avatar, inputStream);

   
        repository.save(avatar);

        return avatar;
    }
}