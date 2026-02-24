package com.example.products.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.products.models.User;




@Repository
public interface UserRepository extends MongoRepository<User, String> {
  
}