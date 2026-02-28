package com.example.media.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.media.models.ProductImage;
import com.example.media.services.ProductImageService;
import com.example.shared.common.kafka.dtos.media.KafkaConfirmImageEvent;

@Service
public class ProductImagesEvents {

    public final ProductImageService productImageService;

    public ProductImagesEvents(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @KafkaListener(topics = "confirm-image-events", groupId = "media-group")
    public void listenConfirmAvatar(KafkaConfirmImageEvent object) {

        System.out.println("Confirme Image ==============> " + object.id());

        this.productImageService.confirmImage(object.id());
    }

    @KafkaListener(topics = "delete-image-events", groupId = "media-group")
    public void listenDeleteAvatar(KafkaConfirmImageEvent object) {

        System.out.println("Delete Image ==============> " + object.id());

        ProductImage i = this.productImageService.getAvatarbyId(object.id());
        this.productImageService.deleteImage(i);
    }
}
