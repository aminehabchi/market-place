package com.example.products.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class MyConsumer {

    @KafkaListener(topics = "user-events", groupId = "products-group")
    public void listen(String message) {
        System.out.println("==============================================");
        System.out.println("======> " + message + " <===========");
        System.out.println("==============================================");
    }

    @PostConstruct
    public void checkKafka() {
        System.out.println("Kafka consumer bean initialized, ready to receive messages.");
    }
}
