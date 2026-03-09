package com.example.media.services;

import org.springframework.stereotype.Service;

import com.example.media.models.User;
import com.example.media.repositories.UserRepository;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(KafkaUserCreatedEvent object) {
        User u = new User(object.userId());
        u = this.userRepository.save(u);
        System.err.println("new user added ==> " + u.getId());
    }

    public void deleteUser(KafkaUserRemovedEvent object) {
        this.userRepository.deleteById(object.userId());
    }
}
