package com.example.products.services;

import com.example.products.kafka.dto.UserCreatedEvent;
import com.example.products.kafka.dto.UserRemovedEvent;
import com.example.products.kafka.dto.UserUpdatedEvent;
import com.example.products.repositories.ProductRepository;

import main.java.com.example.products.repositories.UserRepository;

@Service
public class UsersService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository=userRepository;
    }

    public void createUser(UserCreatedEvent obj) {
        // create user
    }

    public void updateUser(UserUpdatedEvent obj) {
        // update user
    }

    public void deleteUser(UserRemovedEvent obj) {
        // delete user
        this.productRepository.deleteByUserId(obj.userId());
    }
}
