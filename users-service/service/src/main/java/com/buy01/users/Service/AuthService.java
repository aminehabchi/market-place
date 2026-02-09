package com.buy01.users.Service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.buy01.users.DTOs.LoginReqDTOs;
import com.buy01.users.DTOs.LoginResDTOs;
import com.buy01.users.DTOs.RegisterReqDTOs;
import com.buy01.users.DTOs.RegisterResDTOs;
import com.buy01.users.Entity.User;
import com.buy01.users.Repository.UserRepository;
import com.buy01.users.Utils.JwtUtils;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public RegisterResDTOs register(RegisterReqDTOs req) {
        String role = normalizeRole(req.role());
        User user = new User(null, req.username(), req.email(), passwordEncoder.encode(req.password()), role, null);
        userRepository.save(user);
        return new RegisterResDTOs("user created");
    }

    public LoginResDTOs login(LoginReqDTOs req) {
        User user = userRepository.findByUsernameOrEmail(req.identification(), req.identification())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (passwordEncoder.matches(req.password(), user.password())) {
            String token = jwtUtils.generateToken(user.id(), user.role());
            return new LoginResDTOs(token, user.role(), "ok");
        }
        throw new UsernameNotFoundException("Invalid credentials");
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "CLIENT";
        }
        String normalized = role.trim().toUpperCase();
        if (!normalized.equals("CLIENT") && !normalized.equals("SELLER")) {
            throw new IllegalArgumentException("Invalid role. Use CLIENT or SELLER");
        }
        return normalized;
    }
}
