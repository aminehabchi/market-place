package com.example.media.services;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

import com.example.media.models.UserAvatar;
import com.example.media.repositories.UserRepository;
import com.example.media.repositories.UserAvatarContentStore;

@Service
public class UserAvatarService {

    private final UserRepository repository;
    private final UserAvatarContentStore contentStore;

    public UserAvatarService(UserRepository repository,
            UserAvatarContentStore contentStore) {
        this.repository = repository;
        this.contentStore = contentStore;
    }

    public UserAvatar uploadAvatar(InputStream content) {

        UserAvatar avatar = new UserAvatar();
        avatar.setId(UUID.randomUUID());

        avatar = repository.save(avatar);

        contentStore.setContent(avatar, content);

        return avatar;
    }

    public InputStream getAvatar(UUID avatarId) {
        UserAvatar avatar = repository.findById(avatarId).orElseThrow();
        return contentStore.getContent(avatar);
    }
}