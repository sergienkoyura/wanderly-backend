package com.wanderly.userservice.service;

import com.wanderly.userservice.entity.UserPreferences;

import java.util.UUID;

public interface UserPreferencesService {
    boolean existsByUserId(UUID userId);

    void save(UserPreferences userPreferences);
}
