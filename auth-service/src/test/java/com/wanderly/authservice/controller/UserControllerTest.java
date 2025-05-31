package com.wanderly.authservice.controller;

import com.wanderly.authservice.dto.response.UserDto;
import com.wanderly.authservice.service.UserService;
import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.exception.BadRequestException;
import com.wanderly.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;
    @InjectMocks private UserController userController;

    @Test
    void me_ReturnsUserDto_WhenTokenIsValid() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String token = "Bearer mock-token";

        UserDto expectedDto = new UserDto();
        expectedDto.setEmail("test@test.com");
        expectedDto.setCreatedAt(LocalDateTime.now());

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);
            when(userService.findDtoById(userId)).thenReturn(expectedDto);

            // Act
            ResponseEntity<CustomResponse<UserDto>> response = userController.me(token);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("User is found");
            assertThat(response.getBody().getData()).isEqualTo(expectedDto);
        }
    }

    @Test
    void me_ThrowsException_WhenTokenIsMissingOrInvalid() {
        // Arrange
        String token = null;

        // Act
        assertThatThrownBy(() -> userController.me(token))
                .isInstanceOf(BadRequestException.class);
    }
}
