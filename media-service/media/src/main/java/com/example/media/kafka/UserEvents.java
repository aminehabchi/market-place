package com.example.media.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;


import jakarta.annotation.PostConstruct;

@Service
public class UserEvents {

    private final UsersService usersService;

    public UserEvents(UsersService usersService) {
        this.usersService = usersService;
    }

    @KafkaListener(topics = "create-user-events", groupId = "products-group")
    public void listenCreateUser(KafkaUserCreatedEvent object) {
        System.out.println("==============================================");
        System.out.println("Create User Event: " + object.toString());
        System.out.println("==============================================");

        this.usersService.createUser(object);
    }

    
    @KafkaListener(topics = "remove-user-events", groupId = "products-group")
    public void listenRemoveUser(KafkaUserRemovedEvent object) {
        System.out.println("==============================================");
        System.out.println("Remove User Event: " + object.toString());
        System.out.println("==============================================");

        this.usersService.deleteUser(object);
    }
}
