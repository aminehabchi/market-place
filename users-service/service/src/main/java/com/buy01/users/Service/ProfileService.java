package com.buy01.users.Service;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.buy01.users.DTOs.ProfileResDTOs;
import com.buy01.users.DTOs.ProfileUpdateReqDTOs;
import com.buy01.users.DTOs.RegisterResDTOs;
import com.buy01.users.Entity.User;
import com.buy01.users.Repository.UserRepository;
import com.example.shared.common.kafka.dtos.users.KafkaUserUpdatedEvent;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProfileService(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ProfileResDTOs getCurrentProfile() {
        User user = getAuthenticatedUser();
        return new ProfileResDTOs(user.id(), user.name(), user.email(), user.role(), user.avatarUrl());
    }

    public ProfileResDTOs updateCurrentProfile(ProfileUpdateReqDTOs req) {
        User user = getAuthenticatedUser();

        String updatedName = req.name() == null || req.name().isBlank() ? user.name() : req.name();
        String updatedEmail = req.email() == null || req.email().isBlank() ? user.email() : req.email();
        UUID updatedAvatarUrl = user.avatarUrl();

        if (req.uuid() != null) {
            if (!"SELLER".equalsIgnoreCase(user.role())) {
                throw new IllegalArgumentException("Only SELLER can update avatar");
            }
            updatedAvatarUrl = req.uuid();
        }

        User updated = new User(
                user.id(),
                updatedName,
                updatedEmail,
                user.password(),
                user.role(),
                updatedAvatarUrl);

        User newUser = userRepository.save(updated);
        KafkaUserUpdatedEvent event = new KafkaUserUpdatedEvent(newUser.id(), newUser.name(), user.avatarUrl(),
                updatedAvatarUrl);
        kafkaTemplate.send("update-user-events", null, event);
        return new ProfileResDTOs(updated.id(), updated.name(), updated.email(), updated.role(), updated.avatarUrl());
    }

    private User getAuthenticatedUser() {
        String authName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("====================== " + authName);
        return userRepository.findById(authName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found 1"));
    }

    public RegisterResDTOs deleteCurrentUser() {
        String authName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean exist = userRepository.existsById(authName);
        System.out.println("============ existe ============= " + exist);
        if (exist) {
            userRepository.deleteById(authName);
            return new RegisterResDTOs("user deleted successfully");
        }
        throw new UsernameNotFoundException("User not found");
    }
}
