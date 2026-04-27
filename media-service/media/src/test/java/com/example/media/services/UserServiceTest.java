package com.example.media.services;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.media.repositories.UserRepository;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void createUserSavesEntity() {
        userService.createUser(new KafkaUserCreatedEvent("u-1", "Alice", UUID.randomUUID()));

        verify(userRepository).save(org.mockito.ArgumentMatchers.any(com.example.media.models.User.class));
    }

    @Test
    void deleteUserDeletesById() {
        userService.deleteUser(new KafkaUserRemovedEvent("u-1"));

        verify(userRepository).deleteById("u-1");
    }
}
