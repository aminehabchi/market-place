package com.example.products.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.products.services.UsersService;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserUpdatedEvent;

@SuppressWarnings("null")
class ProductsServiceKafkaEventHandlerTest {
    @Mock
    private UsersService usersService;

    private UserEvents userEvents;

    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEvents = new UserEvents(usersService);
        userId = UUID.randomUUID();
        doNothing().when(usersService).createUser(org.mockito.ArgumentMatchers.any(KafkaUserCreatedEvent.class));
        doNothing().when(usersService).updateUser(org.mockito.ArgumentMatchers.any(KafkaUserUpdatedEvent.class));
        doNothing().when(usersService).deleteUser(org.mockito.ArgumentMatchers.any(KafkaUserRemovedEvent.class));
    }

    @Test
    void listenCreateUserDelegatesToUsersService() {
        KafkaUserCreatedEvent event = new KafkaUserCreatedEvent(userId, "Test User", null);

        userEvents.listenCreateUser(event);

        verify(usersService).createUser(event);
        assertEquals(userId, event.userId());
    }

    @Test
    void listenUpdateUserDelegatesToUsersService() {
        KafkaUserUpdatedEvent event = new KafkaUserUpdatedEvent(userId, "Updated User", null);

        userEvents.listenUpdateUser(event);

        verify(usersService).updateUser(event);
        assertEquals(userId, event.userId());
    }

    @Test
    void listenRemoveUserDelegatesToUsersService() {
        KafkaUserRemovedEvent event = new KafkaUserRemovedEvent(userId);

        userEvents.listenRemoveUser(event);

        verify(usersService).deleteUser(event);
        assertEquals(userId, event.userId());
    }
}