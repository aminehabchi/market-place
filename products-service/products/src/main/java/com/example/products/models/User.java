package com.example.products.models;

import com.example.shared.common.database.BaseEntity;
import com.example.shared.common.types.Role;

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
@Document(collection = "products")
public class User extends BaseEntity {
    @Indexed(unique = true)
    String username;

    String avatar;

    Role role;
}
