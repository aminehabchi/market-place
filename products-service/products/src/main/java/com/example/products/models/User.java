package com.example.products.models;

import java.util.UUID;

import com.example.shared.common.types.Role;
import com.example.shared.common.kafka.dtos.users.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    String username;

    UUID avatar;

    Role role;

    public User(KafkaUserCreatedEvent u) {
        this.id = u.userId();
        this.username = u.username();
        this.avatar = u.avatar();
    }

    public void update(KafkaUserUpdatedEvent u) {
        this.username = u.username();
        this.avatar = u.newAvatar();
    }
}
