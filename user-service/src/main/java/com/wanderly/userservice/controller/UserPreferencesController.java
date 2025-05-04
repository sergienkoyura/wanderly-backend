package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.userservice.dto.UserPreferencesDto;
import com.wanderly.userservice.service.UserPreferencesService;
import com.wanderly.common.util.JwtUtil;
import jakarta.validation.Valid;
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

    @GetMapping("/me")
    public ResponseEntity<CustomResponse<?>> me(@RequestHeader("Authorization") String token) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("User preferences are found", userPreferencesService.findDtoByUserId(userId)));
    }


    @PostMapping("/me")
    public ResponseEntity<CustomResponse<?>> save(@RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody UserPreferencesDto userPreferencesDto) {
        UUID userId = JwtUtil.extractUserId(token);
        userPreferencesService.save(userId, userPreferencesDto);

        return ResponseEntity.ok(ResponseFactory.success("User has been saved", null));
    }

}
