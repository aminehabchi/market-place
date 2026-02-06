package com.buy01.users.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.buy01.users.Entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}