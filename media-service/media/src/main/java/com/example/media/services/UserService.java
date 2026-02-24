package com.example.media.services;

import org.springframework.stereotype.Service;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;

@Service
public class UserService {

    public UserService() {

    }

    public void createUser(KafkaUserCreatedEvent object) {
    }

    public void deleteUser(KafkaUserRemovedEvent object) {
    }
}
