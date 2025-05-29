package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.userservice.dto.UserProfileDto;
import com.wanderly.userservice.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock private UserProfileService userProfileService;
    @InjectMocks private UserProfileController controller;

    @Test
    void me_returnsUserProfile() {
        String token = "Bearer token";
        UUID userId = UUID.randomUUID();

        when(userProfileService.findDtoByUserId(userId)).thenReturn(null);

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);

            ResponseEntity<CustomResponse<?>> response = controller.me(token);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("User profile found");
            verify(userProfileService).findDtoByUserId(userId);
        }
    }

    @Test
    void save_savesAndReturnsUserProfile() {
        String token = "Bearer token";
        UUID userId = UUID.randomUUID();
        UserProfileDto dto = new UserProfileDto();
        dto.setName("Bob");
        dto.setAvatarName(null);

        when(userProfileService.save(userId, dto)).thenReturn(dto);

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);

            ResponseEntity<CustomResponse<UserProfileDto>> response = controller.save(token, dto);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("User profile is saved");
            verify(userProfileService).save(userId, dto);
        }
    }
}
