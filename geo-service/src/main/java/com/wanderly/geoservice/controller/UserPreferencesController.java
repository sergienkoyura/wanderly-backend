package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.geoservice.dto.UserPreferencesDto;
import com.wanderly.geoservice.service.UserPreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/geo")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @GetMapping("/me")
    public ResponseEntity<CustomResponse<?>> me(@RequestHeader("Authorization") String token) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("User preferences are found", userPreferencesService.findDtoByUserId(userId)));
    }


    @PostMapping("/me")
    public ResponseEntity<CustomResponse<UserPreferencesDto>> save(@RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody UserPreferencesDto userPreferencesDto) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("User has been saved", userPreferencesService.save(userId, userPreferencesDto)));
    }
}
