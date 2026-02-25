package com.example.media.repositories;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.media.models.UserAvatar;

@Repository
public interface AvatarRepository extends MongoRepository<UserAvatar, UUID> {

}