package com.buy01.users.Service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.buy01.users.DTOs.LoginReqDTOs;
import com.buy01.users.DTOs.LoginResDTOs;
import com.buy01.users.DTOs.RegisterReqDTOs;
import com.buy01.users.DTOs.RegisterResDTOs;
import com.buy01.users.DTOs.UserCreatedEvent;
import com.buy01.users.Entity.User;
import com.buy01.users.Repository.UserRepository;
import com.buy01.users.Utils.JwtUtils;
import org.springframework.kafka.core.KafkaTemplate;
import com.example.shared.common.kafkaDtos.KafkaUserCreatedEvent;
import com.example.shared.common.types.Role;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.kafkaTemplate = kafkaTemplate;
    }

    public RegisterResDTOs register(RegisterReqDTOs req) {
        Role role = normalizeRole(req.role());
        User user = new User(null, req.username(), req.email(), passwordEncoder.encode(req.password()), role.toString().substring(5), null);
        userRepository.save(user);
        KafkaUserCreatedEvent event = new KafkaUserCreatedEvent(null, user.email(), user.username());
        kafkaTemplate.send("create-user-events", null, event);
        return new RegisterResDTOs("user created");
    }

    public LoginResDTOs login(LoginReqDTOs req) {
        User user = userRepository.findByUsernameOrEmail(req.identification(), req.identification())
                .orElseThrow(() -> new UsernameNotFoundException("User not found 1"));
        if (passwordEncoder.matches(req.password(), user.password())) {
            String token = jwtUtils.generateToken(user.id(), user.role());
            return new LoginResDTOs(token, user.role(), "ok");
        }
        throw new UsernameNotFoundException("Invalid credentials");
    }

    private Role normalizeRole(String roleInput) {
        if (roleInput == null || roleInput.isBlank()) {
            return Role.ROLE_BUYER;
        }

        String normalized = roleInput.trim().toUpperCase();

        return switch (normalized) {
            case "CLIENT", "BUYER" -> Role.ROLE_BUYER;
            case "SELLER" -> Role.ROLE_SELLER;
            case "ADMIN" -> Role.ROLE_ADMIN;
            case "GUEST" -> Role.ROLE_GUEST;
            default -> throw new IllegalArgumentException(
                    "Invalid role. Use CLIENT or SELLER");
        };
    }
}
