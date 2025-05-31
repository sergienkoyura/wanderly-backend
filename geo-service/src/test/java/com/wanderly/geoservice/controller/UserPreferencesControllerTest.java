package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.geoservice.dto.UserPreferencesDto;
import com.wanderly.geoservice.service.UserPreferencesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferencesControllerTest {

    @Mock private UserPreferencesService userPreferencesService;
    @InjectMocks private UserPreferencesController controller;

    @Test
    void me_ReturnsUserPreferences_WhenTokenValid() {
        UUID userId = UUID.randomUUID();
        UserPreferencesDto preferencesDto = new UserPreferencesDto();

        try (var jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId("token")).thenReturn(userId);
            when(userPreferencesService.findDtoByUserId(userId)).thenReturn(preferencesDto);

            ResponseEntity<CustomResponse<?>> response = controller.me("token");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("User preferences are found");
            assertThat(response.getBody().getData()).isEqualTo(preferencesDto);
        }
    }

    @Test
    void save_SavesPreferencesSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserPreferencesDto inputDto = new UserPreferencesDto();
        UserPreferencesDto savedDto = new UserPreferencesDto();

        try (var jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId("token")).thenReturn(userId);
            when(userPreferencesService.save(userId, inputDto)).thenReturn(savedDto);

            ResponseEntity<CustomResponse<UserPreferencesDto>> response = controller.save("token", inputDto);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("User has been saved");
            assertThat(response.getBody().getData()).isEqualTo(savedDto);

            verify(userPreferencesService).save(userId, inputDto);
        }
    }
}
