package com.example.products.services;

import org.springframework.stereotype.Service;

import com.example.shared.common.kafkaDtos.KafkaUserCreatedEvent;
import com.example.shared.common.kafkaDtos.KafkaUserUpdatedEvent;
import com.example.shared.common.kafkaDtos.KafkaUserRemovedEvent;

import com.example.products.repositories.ProductRepository;

import com.example.products.repositories.UserRepository;

@Service
public class UsersService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public UsersService(ProductRepository productRepository,UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository=userRepository;
    }

    public void createUser(KafkaUserCreatedEvent obj) {
        // create user
    }

    public void updateUser(KafkaUserUpdatedEvent obj) {
        // update user
    }

    public void deleteUser(KafkaUserRemovedEvent obj) {
        // delete user
        this.productRepository.deleteByUserId(obj.userId());
    }
}
