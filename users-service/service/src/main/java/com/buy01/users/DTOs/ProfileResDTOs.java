package com.buy01.users.DTOs;

public record ProfileResDTOs(
        String id,
        String username,
        String email,
        String role,
        String avatarUrl) {
}
