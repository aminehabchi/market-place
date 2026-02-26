package com.example.media.configs;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

        @Value("${spring.data.mongodb.uri}")
        private String mongoUri;

        @Bean
        public MongoClient mongoClient() {
                ConnectionString connectionString = new ConnectionString(mongoUri);
                MongoClientSettings settings = MongoClientSettings.builder()
                                .applyConnectionString(connectionString)
                                .uuidRepresentation(UuidRepresentation.STANDARD)
                                .build();
                return MongoClients.create(settings);
        }

        @Bean
        public MongoTemplate mongoTemplate() {
                return new MongoTemplate(mongoClient(), "media-db");
        }
}