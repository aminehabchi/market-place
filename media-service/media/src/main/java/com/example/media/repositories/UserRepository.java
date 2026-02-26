package com.example.media.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.media.models.User;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

}