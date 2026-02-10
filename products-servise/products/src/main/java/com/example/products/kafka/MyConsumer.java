package com.example.products.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MyConsumer {

    @KafkaListener(topics = "products-group")
    public void listen(String message) {
        System.out.println(message);
    }
}
