package com.buy01.users.DTOs;

import java.util.UUID;

public record ProfileResDTOs(
        String id,
        String username,
        String email,
        String role,
        UUID avatarUrl) {
}
