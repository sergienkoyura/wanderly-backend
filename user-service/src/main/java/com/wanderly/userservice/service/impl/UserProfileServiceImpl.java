package com.wanderly.userservice.service.impl;

import com.wanderly.userservice.dto.UserProfileDto;
import com.wanderly.userservice.entity.UserProfile;
import com.wanderly.userservice.exception.UserProfileNotFound;
import com.wanderly.common.exception.ExistsByUserIdException;
import com.wanderly.userservice.mapper.UserProfileMapper;
import com.wanderly.userservice.repository.UserProfileRepository;
import com.wanderly.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfileDto save(UUID userId, UserProfileDto userProfileDto) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());
        profile.setUserId(userId);
        profile.setName(userProfileDto.getName());
        profile.setAvatarName(userProfileDto.getAvatarName());

        return userProfileMapper.toUserProfileDto(userProfileRepository.save(profile));
    }

    @Override
    public UserProfileDto findDtoByUserId(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(UserProfileNotFound::new);

        return userProfileMapper.toUserProfileDto(profile);
    }
}
