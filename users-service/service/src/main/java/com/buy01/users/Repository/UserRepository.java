package com.buy01.users.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.buy01.users.Entity.User;

public interface UserRepository extends MongoRepository<User, String> {
}