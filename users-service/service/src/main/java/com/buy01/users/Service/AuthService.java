package com.buy01.users.Service;

import org.springframework.stereotype.Service;

import com.buy01.users.DTOs.RegisterReqDTOs;
import com.buy01.users.DTOs.RegisterResDTOs;
import com.buy01.users.Entity.User;
import com.buy01.users.Repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResDTOs register(RegisterReqDTOs req) {
        User user = new User(null, req.username(), req.email(), req.password(), null);
        userRepository.save(user);
        return new RegisterResDTOs("user created succesfully");
    }
}
