package com.wanderly.userservice.service;

import com.wanderly.userservice.entity.UserPreferences;

import java.util.UUID;

public interface UserPreferencesService {
    boolean existsByUserId(UUID userId);

    UserPreferences save(UserPreferences userPreferences);

    UserPreferences findById(UUID preferencesId);
}
