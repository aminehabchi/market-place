package com.example.products.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.TestPropertySource;

import com.example.products.services.UsersService;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserUpdatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "logging.level.org.apache.kafka=WARN"
})
@SuppressWarnings("null")
class ProductsServiceKafkaEventHandlerTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private UsersService usersService;

    @Autowired
    private UserEvents userEvents;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        doNothing().when(usersService).createUser(any(KafkaUserCreatedEvent.class));
        doNothing().when(usersService).updateUser(any(KafkaUserUpdatedEvent.class));
        doNothing().when(usersService).deleteUser(any(KafkaUserRemovedEvent.class));
    }

    @Test
    void testUserCreatedEventListening() throws InterruptedException {
        KafkaUserCreatedEvent event = new KafkaUserCreatedEvent(userId, "Test User", null);

        kafkaTemplate.send("create-user-events", event);

        // Small delay to allow Kafka listener to process
        Thread.sleep(2000);

        ArgumentCaptor<KafkaUserCreatedEvent> captor = ArgumentCaptor.forClass(KafkaUserCreatedEvent.class);
        verify(usersService).createUser(captor.capture());

        KafkaUserCreatedEvent capturedEvent = captor.getValue();
        assert capturedEvent.userId().equals(userId);
        assert "Test User".equals(capturedEvent.name());
    }

    @Test
    void testUserUpdatedEventListening() throws InterruptedException {
        KafkaUserUpdatedEvent event = new KafkaUserUpdatedEvent(userId, "Updated User", null);

        kafkaTemplate.send("update-user-events", event);

        Thread.sleep(2000);

        ArgumentCaptor<KafkaUserUpdatedEvent> captor = ArgumentCaptor.forClass(KafkaUserUpdatedEvent.class);
        verify(usersService).updateUser(captor.capture());

        KafkaUserUpdatedEvent capturedEvent = captor.getValue();
        assert capturedEvent.userId().equals(userId);
        assert "Updated User".equals(capturedEvent.name());
    }

    @Test
    void testUserRemovedEventListening() throws InterruptedException {
        KafkaUserRemovedEvent event = new KafkaUserRemovedEvent(userId);

        kafkaTemplate.send("remove-user-events", event);

        Thread.sleep(2000);

        ArgumentCaptor<KafkaUserRemovedEvent> captor = ArgumentCaptor.forClass(KafkaUserRemovedEvent.class);
        verify(usersService).deleteUser(captor.capture());

        KafkaUserRemovedEvent capturedEvent = captor.getValue();
        assert capturedEvent.userId().equals(userId);
    }

    @Test
    void testMultipleUserEventsInSequence() throws InterruptedException {
        KafkaUserCreatedEvent createEvent = new KafkaUserCreatedEvent(userId, "New User", null);
        KafkaUserUpdatedEvent updateEvent = new KafkaUserUpdatedEvent(userId, "Updated User", null);
        KafkaUserRemovedEvent removeEvent = new KafkaUserRemovedEvent(userId);

        kafkaTemplate.send("create-user-events", createEvent);
        Thread.sleep(1000);
        kafkaTemplate.send("update-user-events", updateEvent);
        Thread.sleep(1000);
        kafkaTemplate.send("remove-user-events", removeEvent);

        Thread.sleep(2000);

        verify(usersService, times(1)).createUser(any(KafkaUserCreatedEvent.class));
        verify(usersService, times(1)).updateUser(any(KafkaUserUpdatedEvent.class));
        verify(usersService, times(1)).deleteUser(any(KafkaUserRemovedEvent.class));
    }
}
