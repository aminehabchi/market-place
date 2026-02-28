package com.buy01.users.DTOs;

import java.util.UUID;

public record ProfileResDTOs(
                String id,
                String username,
                String email,
                String role,
                UUID avatarUrl) {
        public ProfileResDTOs(String id, String username, String email, String role, String avatarUrl) {
                this(id, username, email, role,
                                (avatarUrl != null && !avatarUrl.isBlank()) ? UUID.fromString(avatarUrl) : null);
        }
}