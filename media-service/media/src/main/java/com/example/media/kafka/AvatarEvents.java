package com.example.media.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.media.models.UserAvatar;
import com.example.media.services.AvatarService;
import com.example.shared.common.kafka.dtos.media.KafkaConfirmAvatarEvent;

@Service
public class AvatarEvents {

    private final AvatarService avatarService;

    public AvatarEvents(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @KafkaListener(topics = "confirm-avatar-events", groupId = "media-group")
    public void listenConfirmAvatar(KafkaConfirmAvatarEvent object) {
        this.avatarService.confirmAvatar(object.id());
    }

    @KafkaListener(topics = "delete-avatar-events", groupId = "media-group")
    public void listenDeleteAvatar(KafkaConfirmAvatarEvent object) {
        UserAvatar i = this.avatarService.getAvatarbyId(object.id());
        this.avatarService.deleteAvatar(i);
    }

}
