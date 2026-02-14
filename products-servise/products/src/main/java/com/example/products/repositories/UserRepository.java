package com.example.products.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.products.models.User;


@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
  
}