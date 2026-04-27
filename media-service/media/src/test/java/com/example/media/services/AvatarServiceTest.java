package com.example.media.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.media.models.UserAvatar;
import com.example.media.repositories.AvatarRepository;
import com.example.media.stores.UserAvatarContentStore;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AvatarServiceTest {

    @Mock
    private AvatarRepository repository;

    @Mock
    private UserAvatarContentStore contentStore;

    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        avatarService = new AvatarService(repository, contentStore);
    }

    @Test
    void isImageMimeTypeHandlesNullAndImagePrefix() {
        assertFalse(avatarService.isImageMimeType(null));
        assertTrue(avatarService.isImageMimeType("image/png"));
    }

    @Test
    void deleteAvatarByUserIdDeletesContentAndEntity() {
        UserAvatar avatar = new UserAvatar();
        when(repository.findByUserId("u-1")).thenReturn(avatar);

        avatarService.deleteAvatarByUserId("u-1");

        verify(contentStore).unsetContent(avatar);
        verify(repository).delete(avatar);
    }

    @Test
    void confirmAvatarUpdatesStatusWhenFound() {
        UUID id = UUID.randomUUID();
        UserAvatar avatar = new UserAvatar();

        when(repository.findById(id)).thenReturn(Optional.of(avatar));

        avatarService.confirmAvatar(id);

        verify(repository).save(any(UserAvatar.class));
    }
}
