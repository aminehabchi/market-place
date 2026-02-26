package com.example.products.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.products.models.Product;
import com.example.products.models.User;
import com.example.shared.common.kafkaDtos.KafkaUserCreatedEvent;
import com.example.shared.common.kafkaDtos.KafkaUserUpdatedEvent;
import com.example.shared.common.kafkaDtos.KafkaUserRemovedEvent;

import com.example.products.repositories.ProductRepository;

import com.example.products.repositories.UserRepository;

@Service
public class UsersService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public UsersService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void createUser(KafkaUserCreatedEvent obj) {
        User u = new User(obj);
        this.userRepository.save(u);
    }

    public void updateUser(KafkaUserUpdatedEvent obj) {
        User u = this.userRepository.findAllById(obj.userId());
        u.update(obj);
        this.userRepository.save(u);
    }

    public void deleteUser(KafkaUserRemovedEvent obj) {
        List<Product> products = this.productRepository.findByUserId(obj.userId());

        for (Product p : products) {
            // *********************************//
            //  must send event to media sevice 
            // *********************************//
        }

        this.productRepository.deleteByUserId(obj.userId());
        this.userRepository.deleteById(obj.userId());
    }
}
