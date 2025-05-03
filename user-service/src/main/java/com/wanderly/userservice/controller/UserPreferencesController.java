package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.userservice.dto.UserPreferencesDto;
import com.wanderly.userservice.entity.UserPreferences;
import com.wanderly.userservice.kafka.CityLookupProducer;
import com.wanderly.userservice.mapper.UserPreferencesMapper;
import com.wanderly.userservice.service.UserPreferencesService;
import com.wanderly.userservice.util.JwtUtil;
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
    private final UserPreferencesMapper userPreferencesMapper;
    private final CityLookupProducer cityLookupProducer;

//    @GetMapping("/me")

    @GetMapping("/exists")
    public ResponseEntity<CustomResponse<Boolean>> exists(@RequestHeader("Authorization") String token) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("User is checked", userPreferencesService.existsByUserId(userId)));

    }


    @PostMapping("/me")
    public ResponseEntity<CustomResponse<?>> save(@RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody UserPreferencesDto userPreferencesDto) {
        UUID userId = JwtUtil.extractUserId(token);

        UserPreferences userPreferences = userPreferencesMapper.toUserPreferences(userPreferencesDto);
        userPreferences.setUserId(userId);

        UserPreferences saved = userPreferencesService.save(userPreferences);

        userPreferencesDto.getCity().setPreferencesId(saved.getId());
        cityLookupProducer.sendCityLookupRequest(userPreferencesDto.getCity());

        return ResponseEntity.ok(ResponseFactory.success("User has been saved", null));
    }

}
