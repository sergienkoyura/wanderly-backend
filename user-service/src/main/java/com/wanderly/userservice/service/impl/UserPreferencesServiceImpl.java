package com.wanderly.userservice.service.impl;

import com.wanderly.userservice.UserPreferencesNotFound;
import com.wanderly.userservice.entity.UserPreferences;
import com.wanderly.userservice.repository.UserPreferencesRepository;
import com.wanderly.userservice.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {
    private final UserPreferencesRepository userPreferencesRepository;

    @Override
    public boolean existsByUserId(UUID userId) {
        return userPreferencesRepository.existsByUserId(userId);
    }

    @Override
    public UserPreferences save(UserPreferences userPreferences) {
        return userPreferencesRepository.save(userPreferences);
    }

    @Override
    public UserPreferences findById(UUID preferencesId) {
        return userPreferencesRepository.findById(preferencesId)
                .orElseThrow(UserPreferencesNotFound::new);
    }
}
