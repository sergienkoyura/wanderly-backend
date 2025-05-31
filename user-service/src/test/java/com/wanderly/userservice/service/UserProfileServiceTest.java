package com.wanderly.userservice.service;

import com.wanderly.userservice.dto.UserProfileDto;
import com.wanderly.userservice.entity.UserProfile;
import com.wanderly.userservice.exception.UserProfileNotFound;
import com.wanderly.userservice.mapper.UserProfileMapper;
import com.wanderly.userservice.repository.UserProfileRepository;
import com.wanderly.userservice.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock private UserProfileRepository userProfileRepository;
    @Mock private UserProfileMapper userProfileMapper;
    @InjectMocks private UserProfileServiceImpl userProfileService;

    @Test
    void save_shouldCreateNewProfileIfNotExist() {
        UUID userId = UUID.randomUUID();
        UserProfileDto inputDto = new UserProfileDto();
        UserProfile entityToSave = new UserProfile();
        entityToSave.setUserId(userId);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(entityToSave);
        when(userProfileMapper.toUserProfileDto(any(UserProfile.class))).thenReturn(inputDto);

        UserProfileDto result = userProfileService.save(userId, inputDto);

        assertThat(result).isEqualTo(inputDto);
        verify(userProfileRepository).save(any());
    }

    @Test
    void save_shouldUpdateExistingProfile() {
        UUID userId = UUID.randomUUID();
        UserProfile existing = new UserProfile();
        existing.setUserId(userId);
        existing.setName("Old Name");

        UserProfileDto inputDto = new UserProfileDto();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(existing)).thenReturn(existing);
        when(userProfileMapper.toUserProfileDto(existing)).thenReturn(inputDto);

        userProfileService.save(userId, inputDto);

        verify(userProfileRepository).save(existing);
    }

    @Test
    void findDtoByUserId_shouldReturnDtoIfFound() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setName("Bob");

        UserProfileDto expectedDto = new UserProfileDto();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userProfileMapper.toUserProfileDto(profile)).thenReturn(expectedDto);

        UserProfileDto result = userProfileService.findDtoByUserId(userId);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void findDtoByUserId_shouldThrowIfNotFound() {
        UUID userId = UUID.randomUUID();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UserProfileNotFound.class, () -> userProfileService.findDtoByUserId(userId));
    }
}
