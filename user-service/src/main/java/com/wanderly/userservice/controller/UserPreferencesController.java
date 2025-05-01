package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.userservice.entity.UserPreferences;
import com.wanderly.userservice.service.UserPreferencesService;
import com.wanderly.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

//    @GetMapping("/me")

    @GetMapping("/exists")
    public ResponseEntity<CustomResponse<Boolean>> exists(@RequestHeader("Authorization") String token) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("User is checked", userPreferencesService.existsByUserId(userId)));

    }


    //todo: validation
    @PostMapping("/me")
    public ResponseEntity<CustomResponse<?>> save(@RequestHeader("Authorization") String token,
                                                  @RequestBody UserPreferences userPreferences) {
        UUID userId = JwtUtil.extractUserId(token);

        userPreferences.setUserId(userId);
        userPreferencesService.save(userPreferences);
        return ResponseEntity.ok(ResponseFactory.success("User has been saved", null));
    }

}
