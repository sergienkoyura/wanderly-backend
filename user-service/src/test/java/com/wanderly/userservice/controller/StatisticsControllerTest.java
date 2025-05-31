package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.userservice.service.StatisticsService;
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
class StatisticsControllerTest {

    @Mock private StatisticsService statisticsService;
    @InjectMocks private StatisticsController controller;

    @Test
    void getStatistics_returnsCustomResponse() {
        String token = "Bearer test";
        UUID userId = UUID.randomUUID();

        when(statisticsService.getStatistics(userId)).thenReturn(null);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);

            ResponseEntity<CustomResponse<?>> response = controller.getStatistics(token);

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            verify(statisticsService).getStatistics(userId);
        }
    }
}
