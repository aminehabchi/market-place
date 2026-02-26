package com.example.media.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.media.services.AvatarService;
import com.example.media.services.UserService;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;

@Service
public class UserEvents {

    private final UserService usersService;
    private final AvatarService avatarService;

    public UserEvents(UserService usersService, AvatarService avatarService) {
        this.usersService = usersService;
        this.avatarService = avatarService;
    }

    @KafkaListener(topics = "create-user-events", groupId = "products-group")
    public void listenCreateUser(KafkaUserCreatedEvent object) {
        this.usersService.createUser(object);
    }

    @KafkaListener(topics = "remove-user-events", groupId = "products-group")
    public void listenRemoveUser(KafkaUserRemovedEvent object) {

        this.avatarService.deleteAvatarByUserId(object.userId());

        this.usersService.deleteUser(object);
    }
}
