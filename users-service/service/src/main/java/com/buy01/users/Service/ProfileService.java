package com.buy01.users.Service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.buy01.users.DTOs.ProfileResDTOs;
import com.buy01.users.DTOs.ProfileUpdateReqDTOs;
import com.buy01.users.Entity.User;
import com.buy01.users.Repository.UserRepository;

@Service
public class ProfileService {
    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileResDTOs getCurrentProfile() {
        User user = getAuthenticatedUser();
        return new ProfileResDTOs(user.id(), user.username(), user.email(), user.role(), user.avatarUrl());
    }

    public ProfileResDTOs updateCurrentProfile(ProfileUpdateReqDTOs req) {
        User user = getAuthenticatedUser();

        String updatedUsername = req.username() == null || req.username().isBlank() ? user.username() : req.username();
        String updatedEmail = req.email() == null || req.email().isBlank() ? user.email() : req.email();
        String updatedAvatarUrl = user.avatarUrl();

        if (req.avatarUrl() != null && !req.avatarUrl().isBlank()) {
            if (!"SELLER".equalsIgnoreCase(user.role())) {
                throw new IllegalArgumentException("Only SELLER can update avatar");
            }
            updatedAvatarUrl = req.avatarUrl();
        }

        User updated = new User(
                user.id(),
                updatedUsername,
                updatedEmail,
                user.password(),
                user.role(),
                updatedAvatarUrl);

        userRepository.save(updated);
        return new ProfileResDTOs(updated.id(), updated.username(), updated.email(), updated.role(), updated.avatarUrl());
    }

    private User getAuthenticatedUser() {
        String authName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(authName, authName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found 1"));
    }
}
