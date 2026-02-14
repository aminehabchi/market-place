package com.example.products.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.buy01.users.DTOs.UserCreatedEvent;
import com.example.products.kafka.dto.UserRemovedEvent;
import com.example.products.services.UsersService;

import jakarta.annotation.PostConstruct;
import main.java.com.example.products.kafka.dto.UserUpdatedEvent;

@Service
public class UserEvents {
    private final UsersService usersService;

    public UsersService(UsersService usersService){
        this.usersService=usersService;
    }

    @KafkaListener(topics = "create-user-events", groupId = "products-group")
    public void listenCreateUser(UserCreatedEvent object) {
        System.out.println("==============================================");
        System.out.println("Create User Event: " + object.toString());
        System.out.println("==============================================");

        this.usersService.createUser(object);
    }

    @KafkaListener(topics = "update-user-events", groupId = "products-group")
    public void listenUpdateUser(UserUpdatedEvent object) {
        System.out.println("==============================================");
        System.out.println("Update User Event: " + object.toString());
        System.out.println("==============================================");

        this.usersService.updateUser(object);
    }

    @KafkaListener(topics = "remove-user-events", groupId = "products-group")
    public void listenRemoveUser(UserRemovedEvent object) {
        System.out.println("==============================================");
        System.out.println("Remove User Event: " + object.toString());
        System.out.println("==============================================");

        this.usersService.deleteUser(object);
    }

    @PostConstruct
    public void init() {
        System.out.println("Kafka consumer bean initialized, ready to receive messages.");
    }
}
