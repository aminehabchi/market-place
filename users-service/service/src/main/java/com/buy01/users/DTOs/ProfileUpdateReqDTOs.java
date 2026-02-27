package com.buy01.users.DTOs;

import java.util.UUID;

public record ProfileUpdateReqDTOs(
                String name,
                String email,
                UUID uuid) {
}
