package com.buy01.users.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.buy01.users.DTOs.ProfileResDTOs;
import com.buy01.users.DTOs.ProfileUpdateReqDTOs;
import com.buy01.users.Service.ProfileService;

@RestController
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResDTOs> getMe() {
        return ResponseEntity.ok(profileService.getCurrentProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResDTOs> updateMe(@RequestBody ProfileUpdateReqDTOs req) {
        return ResponseEntity.ok(profileService.updateCurrentProfile(req));
    }
}
