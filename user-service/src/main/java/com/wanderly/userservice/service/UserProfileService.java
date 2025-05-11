package com.wanderly.userservice.service;

import com.wanderly.userservice.dto.UserProfileDto;

import java.util.UUID;

public interface UserProfileService {
    UserProfileDto save(UUID userId, UserProfileDto userProfileDto);

    UserProfileDto findDtoByUserId(UUID userId);
}
