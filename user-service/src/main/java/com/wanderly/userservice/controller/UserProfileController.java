package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.userservice.dto.UserProfileDto;
import com.wanderly.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<CustomResponse<?>> me(@RequestHeader("Authorization") String token) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("User profile found", userProfileService.findDtoByUserId(userId)));
    }


    @PostMapping("/me")
    public ResponseEntity<CustomResponse<UserProfileDto>> save(@RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody UserProfileDto userProfileDto) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("User profile is saved", userProfileService.save(userId, userProfileDto)));
    }

}
