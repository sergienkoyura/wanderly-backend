package com.wanderly.userservice.service;

import com.wanderly.userservice.dto.UserPreferencesDto;
import com.wanderly.userservice.entity.UserPreferences;

import java.util.UUID;

public interface UserPreferencesService {
    boolean existsByUserId(UUID userId);

    void save(UUID userId, UserPreferencesDto userPreferencesDto);

    UserPreferences findById(UUID preferencesId);

    UserPreferencesDto findDtoByUserId(UUID userId);
}
