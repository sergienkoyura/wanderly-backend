package com.wanderly.geoservice.service.impl;

import com.wanderly.geoservice.dto.UserPreferencesDto;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.UserPreferences;
import com.wanderly.geoservice.exception.UserPreferencesNotFound;
import com.wanderly.geoservice.mapper.UserPreferencesMapper;
import com.wanderly.geoservice.repository.UserPreferencesRepository;
import com.wanderly.geoservice.service.CityService;
import com.wanderly.geoservice.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserPreferencesMapper userPreferencesMapper;
    private final CityService cityService;

    @Override
    public UserPreferencesDto findDtoByUserId(UUID userId) {
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElseThrow(UserPreferencesNotFound::new);

        return userPreferencesMapper.toDto(preferences);
    }

    @Override
    public UserPreferencesDto save(UUID userId, UserPreferencesDto userPreferencesDto) {
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElse(new UserPreferences());

        City city = cityService.findByOsmId(userPreferencesDto.getCity());

        preferences.setUserId(userId);
        preferences.setCity(city);
        preferences.setTravelType(userPreferencesDto.getTravelType());
        preferences.setActivityType(userPreferencesDto.getActivityType());
        preferences.setTimePerRoute(userPreferencesDto.getTimePerRoute());

        return userPreferencesMapper.toDto(userPreferencesRepository.save(preferences));
    }

    @Override
    public UserPreferences findByUserId(UUID userId) {
        return userPreferencesRepository.findByUserId(userId)
                .orElseThrow(UserPreferencesNotFound::new);
    }
}
