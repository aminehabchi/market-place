package com.example.media.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import com.example.media.models.UserAvatar;
import com.example.media.models.ProductImage;
import com.example.media.services.AvatarService;
import com.example.media.services.UserService;
import com.example.media.services.ProductService;
import com.example.media.services.ProductImageService;
import com.example.shared.common.kafka.dtos.users.KafkaUserCreatedEvent;
import com.example.shared.common.kafka.dtos.users.KafkaUserRemovedEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductCreatedEvent;
import com.example.shared.common.kafka.dtos.products.KafkaProductRemovedEvent;
import com.example.shared.common.kafka.dtos.media.KafkaConfirmAvatarEvent;
import com.example.shared.common.kafka.dtos.media.KafkaConfirmImageEvent;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "logging.level.org.apache.kafka=WARN"
})
@SuppressWarnings("null")
class MediaServiceKafkaEventHandlerTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AvatarService avatarService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductImageService productImageService;

    private UserEvents userEvents;

    private ProductEvents productEvents;

    private AvatarEvents avatarEvents;

    private ProductImagesEvents productImagesEvents;

    private String userId;
    private UUID productId;
    private UUID imageId;
    private UUID avatarId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        productId = UUID.randomUUID();
        imageId = UUID.randomUUID();
        avatarId = UUID.randomUUID();

        doNothing().when(userService).createUser(any(KafkaUserCreatedEvent.class));
        doNothing().when(userService).deleteUser(any(KafkaUserRemovedEvent.class));
        doNothing().when(avatarService).deleteAvatarByUserId(UUID.class.toString());
        doNothing().when(productService).createProduct(any(KafkaProductCreatedEvent.class));
        doNothing().when(productService).deleteProduct(any(KafkaProductRemovedEvent.class));
        doNothing().when(productImageService).deleteProductImageByProductId(any(UUID.class));
        doNothing().when(avatarService).confirmAvatar(any(UUID.class));
        doNothing().when(productImageService).confirmImage(any(UUID.class));
    }

    // ===== UserEvents Tests =====

    @Test
    void testUserCreatedEventListening() throws InterruptedException {
        KafkaUserCreatedEvent event = new KafkaUserCreatedEvent(userId, "Test User", null);

        kafkaTemplate.send("create-user-events", event);
        Thread.sleep(2000);

        ArgumentCaptor<KafkaUserCreatedEvent> captor = ArgumentCaptor.forClass(KafkaUserCreatedEvent.class);
        verify(userService).createUser(captor.capture());

        KafkaUserCreatedEvent capturedEvent = captor.getValue();
        assert capturedEvent.userId().equals(userId);
        assert "Test User".equals(capturedEvent.username());
    }

    @Test
    void testUserRemovedEventListening() throws InterruptedException {
        KafkaUserRemovedEvent event = new KafkaUserRemovedEvent(userId);

        kafkaTemplate.send("remove-user-events", event);
        Thread.sleep(2000);

        verify(avatarService).deleteAvatarByUserId(userId);
        ArgumentCaptor<KafkaUserRemovedEvent> captor = ArgumentCaptor.forClass(KafkaUserRemovedEvent.class);
        verify(userService).deleteUser(captor.capture());

        KafkaUserRemovedEvent capturedEvent = captor.getValue();
        assert capturedEvent.userId().equals(userId);
    }

    // ===== ProductEvents Tests =====

    @Test
    void testProductCreatedEventListening() throws InterruptedException {
        KafkaProductCreatedEvent event = new KafkaProductCreatedEvent(productId, userId.toString());

        kafkaTemplate.send("create-product-events", event);
        Thread.sleep(2000);

        ArgumentCaptor<KafkaProductCreatedEvent> captor = ArgumentCaptor.forClass(KafkaProductCreatedEvent.class);
        verify(productService).createProduct(captor.capture());

        KafkaProductCreatedEvent capturedEvent = captor.getValue();
        assert capturedEvent.id().equals(productId);
    }

    @Test
    void testProductRemovedEventListening() throws InterruptedException {
        KafkaProductRemovedEvent event = new KafkaProductRemovedEvent(productId);

        kafkaTemplate.send("remove-product-events", event);
        Thread.sleep(2000);

        verify(productImageService).deleteProductImageByProductId(productId);
        ArgumentCaptor<KafkaProductRemovedEvent> captor = ArgumentCaptor.forClass(KafkaProductRemovedEvent.class);
        verify(productService).deleteProduct(captor.capture());

        KafkaProductRemovedEvent capturedEvent = captor.getValue();
        assert capturedEvent.id().equals(productId);
    }

    // ===== AvatarEvents Tests =====

    @Test
    void testAvatarConfirmEventListening() throws InterruptedException {
        KafkaConfirmAvatarEvent event = new KafkaConfirmAvatarEvent(avatarId);

        kafkaTemplate.send("confirm-avatar-events", event);
        Thread.sleep(2000);

        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(avatarService).confirmAvatar(captor.capture());

        UUID capturedId = captor.getValue();
        assert capturedId.equals(avatarId);
    }

    @Test
    void testAvatarDeleteEventListening() throws InterruptedException {
        UserAvatar avatar = new UserAvatar();
        avatar.setId(avatarId);

        when(avatarService.getAvatarbyId(avatarId)).thenReturn(avatar);
        doNothing().when(avatarService).deleteAvatar(avatar);

        KafkaConfirmAvatarEvent event = new KafkaConfirmAvatarEvent(avatarId);

        kafkaTemplate.send("delete-avatar-events", event);
        Thread.sleep(2000);

        verify(avatarService).getAvatarbyId(avatarId);
        verify(avatarService).deleteAvatar(avatar);
    }

    // ===== ProductImagesEvents Tests =====

    @Test
    void testProductImageConfirmEventListening() throws InterruptedException {
        KafkaConfirmImageEvent event = new KafkaConfirmImageEvent(imageId);

        kafkaTemplate.send("confirm-image-events", event);
        Thread.sleep(2000);

        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(productImageService).confirmImage(captor.capture());

        UUID capturedId = captor.getValue();
        assert capturedId.equals(imageId);
    }

    @Test
    void testProductImageDeleteEventListening() throws InterruptedException {
        ProductImage image = new ProductImage();
        image.setId(imageId);

        when(productImageService.getAvatarbyId(imageId)).thenReturn(image);
        doNothing().when(productImageService).deleteImage(image);

        KafkaConfirmImageEvent event = new KafkaConfirmImageEvent(imageId);

        kafkaTemplate.send("delete-image-events", event);
        Thread.sleep(2000);

        verify(productImageService).getAvatarbyId(imageId);
        verify(productImageService).deleteImage(image);
    }

    @Test
    void testMultipleEventProcessing() throws InterruptedException {
        KafkaUserCreatedEvent userEvent = new KafkaUserCreatedEvent(userId, "User", null);
        KafkaProductCreatedEvent productEvent = new KafkaProductCreatedEvent(productId, userId.toString());
        KafkaConfirmAvatarEvent avatarEvent = new KafkaConfirmAvatarEvent(avatarId);

        kafkaTemplate.send("create-user-events", userEvent);
        Thread.sleep(1000);
        kafkaTemplate.send("create-product-events", productEvent);
        Thread.sleep(1000);
        kafkaTemplate.send("confirm-avatar-events", avatarEvent);
        Thread.sleep(2000);

        verify(userService, times(1)).createUser(any(KafkaUserCreatedEvent.class));
        verify(productService, times(1)).createProduct(any(KafkaProductCreatedEvent.class));
        verify(avatarService, times(1)).confirmAvatar(any(UUID.class));
    }
}
