package com.wanderly.userservice.service.impl;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.common.dto.geo.CitySavedResponse;
import com.wanderly.userservice.UserPreferencesNotFound;
import com.wanderly.userservice.dto.UserPreferencesDto;
import com.wanderly.userservice.entity.UserPreferences;
import com.wanderly.userservice.exception.ExistsByUserIdException;
import com.wanderly.userservice.kafka.CityRequestReplyingProducer;
import com.wanderly.userservice.kafka.CitySaveReplyingProducer;
import com.wanderly.userservice.mapper.UserPreferencesMapper;
import com.wanderly.userservice.repository.UserPreferencesRepository;
import com.wanderly.userservice.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {
    private final UserPreferencesRepository userPreferencesRepository;
    private final CityRequestReplyingProducer cityRequestReplyingProducer;
    private final UserPreferencesMapper userPreferencesMapper;
    private final CitySaveReplyingProducer citySaveReplyingProducer;

    @Override
    public boolean existsByUserId(UUID userId) {
        return userPreferencesRepository.existsByUserId(userId);
    }

    @Override
    public void save(UUID userId, UserPreferencesDto userPreferencesDto) {
        if (existsByUserId(userId) && userPreferencesDto.getId() == null) {
            throw new ExistsByUserIdException();
        }

        CitySavedResponse citySavedResponse = citySaveReplyingProducer.saveCity(userId, userPreferencesDto.getCity());
        UserPreferences userPreferences = userPreferencesMapper.toUserPreferences(userPreferencesDto);
        userPreferences.setUserId(userId);
        userPreferences.setCityId(citySavedResponse.getCityId());

        userPreferencesRepository.save(userPreferences);
    }

    @Override
    public UserPreferences findById(UUID preferencesId) {
        return userPreferencesRepository.findById(preferencesId)
                .orElseThrow(UserPreferencesNotFound::new);
    }

    @Override
    public UserPreferencesDto findDtoByUserId(UUID userId) {
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElseThrow(UserPreferencesNotFound::new);

        CityDto cityDto = cityRequestReplyingProducer.requestCityDetails(preferences.getCityId());

        UserPreferencesDto userPreferencesDto = userPreferencesMapper.toUserPreferencesDto(preferences);
        userPreferencesDto.setCity(cityDto);

        return userPreferencesDto;
    }
}
