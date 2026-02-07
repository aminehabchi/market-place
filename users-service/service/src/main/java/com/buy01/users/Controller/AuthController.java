package com.buy01.users.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buy01.users.DTOs.RegisterDTOs;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @PostMapping("/register")
    public String register(@RequestBody RegisterDTOs req) {
        return req.email();
    }
    // @PostMapping("/login")
}
