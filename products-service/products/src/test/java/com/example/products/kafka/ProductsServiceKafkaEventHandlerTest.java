package com.example.products.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.products.services.UsersService;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserUpdatedEvent;

@SuppressWarnings("null")
class ProductsServiceKafkaEventHandlerTest {
    private UsersService usersService;

    private UserEvents userEvents;

    private String userId;

    @BeforeEach
    void setUp() {
        usersService = new FakeUsersService();
        userEvents = new UserEvents(usersService);
        userId = UUID.randomUUID().toString();
    }

    @Test
    void listenCreateUserDelegatesToUsersService() {
        KafkaUserCreatedEvent event = new KafkaUserCreatedEvent(userId.toString(), "Test User", null);
        userEvents.listenCreateUser(event);

        FakeUsersService fake = (FakeUsersService) usersService;
        assertEquals(event, fake.lastCreatedEvent);
        assertEquals(userId, event.userId());
    }

    @Test
    void listenUpdateUserDelegatesToUsersService() {
        KafkaUserUpdatedEvent event = new KafkaUserUpdatedEvent(userId.toString(), "Updated User", null, null);
        userEvents.listenUpdateUser(event);

        FakeUsersService fake = (FakeUsersService) usersService;
        assertEquals(event, fake.lastUpdatedEvent);
        assertEquals(userId, event.userId());
    }

    @Test
    void listenRemoveUserDelegatesToUsersService() {
        KafkaUserRemovedEvent event = new KafkaUserRemovedEvent(userId.toString());
        userEvents.listenRemoveUser(event);

        FakeUsersService fake = (FakeUsersService) usersService;
        assertEquals(event, fake.lastDeletedEvent);
        assertEquals(userId, event.userId());
    }

    // Fake UsersService to avoid Mockito inline mocks on Java 25
    static class FakeUsersService extends UsersService {
        public KafkaUserCreatedEvent lastCreatedEvent;
        public KafkaUserUpdatedEvent lastUpdatedEvent;
        public KafkaUserRemovedEvent lastDeletedEvent;

        public FakeUsersService() {
            super(null, null, null);
        }

        @Override
        public void createUser(KafkaUserCreatedEvent obj) {
            this.lastCreatedEvent = obj;
        }

        @Override
        public void updateUser(KafkaUserUpdatedEvent obj) {
            this.lastUpdatedEvent = obj;
        }

        @Override
        public void deleteUser(KafkaUserRemovedEvent obj) {
            this.lastDeletedEvent = obj;
        }
    }
}