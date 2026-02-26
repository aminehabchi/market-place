package com.example.media.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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

        this.productImageService.confirmImage(object.id());
    }
}
