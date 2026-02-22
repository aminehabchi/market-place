package com.example.media.repositories;

import org.springframework.content.fs.store.FilesystemContentStore;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.media.models.UserAvatar;

@Repository
public interface UserAvatarContentStore extends FilesystemContentStore<UserAvatar, UUID> {
}