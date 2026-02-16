package com.example.products.shared;

import java.util.Arrays;

public enum Role {
    ROLE_GUEST,
    ROLE_SELLER,
    ROLE_BUYER,
    ROLE_ADMIN;

    public boolean isAdmin() {
        return this == ROLE_ADMIN;
    }

    public boolean isBuyer() {
        return this == ROLE_BUYER;
    }

    public boolean isSeller() {
        return this == ROLE_SELLER;
    }

    public boolean isGuest() {
        return this == ROLE_GUEST;
    }

    public static Role fromString(String roleName) {
        if (roleName == null) return ROLE_GUEST;

        return Arrays.stream(Role.values())
                     .filter(r -> r.name().equalsIgnoreCase(roleName))
                     .findFirst()
                     .orElse(ROLE_GUEST);
    }

    public String toPrettyString() {
        String name = this.name().replace("ROLE_", "");
        String lower = name.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
