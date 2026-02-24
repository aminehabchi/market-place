package com.example.products.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserUpdatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;

import com.example.products.services.UsersService;

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

    @KafkaListener(topics = "update-user-events", groupId = "products-group")
    public void listenUpdateUser(KafkaUserUpdatedEvent object) {
        System.out.println("==============================================");
        System.out.println("Update User Event: " + object.toString());
        System.out.println("==============================================");

        this.usersService.updateUser(object);
    }

    @KafkaListener(topics = "remove-user-events", groupId = "products-group")
    public void listenRemoveUser(KafkaUserRemovedEvent object) {
        System.out.println("==============================================");
        System.out.println("Remove User Event: " + object.toString());
        System.out.println("==============================================");

        this.usersService.deleteUser(object);
    }
}
