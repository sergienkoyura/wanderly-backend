package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.userservice.dto.UserRouteCompletionDto;
import com.wanderly.userservice.service.UserARModelCompletionService;
import com.wanderly.userservice.service.UserRouteCompletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompletionControllerTest {

    @Mock private UserRouteCompletionService routeService;
    @Mock private UserARModelCompletionService modelService;
    @InjectMocks private CompletionController controller;

    @Test
    void saveRouteCompletionStep_shouldCallServiceAndReturnSuccess() {
        String token = "Bearer test";
        UUID userId = UUID.randomUUID();
        UserRouteCompletionDto dto = new UserRouteCompletionDto();

        try (MockedStatic<JwtUtil> jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);

            ResponseEntity<CustomResponse<?>> response = controller.saveRouteCompletionStep(token, dto);

            verify(routeService).save(userId, dto);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("Status saved");
        }
    }

    @Test
    void getRouteCompletion_shouldReturnCorrectDto() {
        String token = "Bearer test";
        UUID userId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();
        UserRouteCompletionDto dto = new UserRouteCompletionDto();

        when(routeService.findByRouteId(userId, routeId)).thenReturn(dto);

        try (MockedStatic<JwtUtil> jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);

            ResponseEntity<CustomResponse<UserRouteCompletionDto>> response = controller.getRouteCompletion(token, routeId);

            verify(routeService).findByRouteId(userId, routeId);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("Completion for the route is found");
            assertThat(response.getBody().getData()).isEqualTo(dto);
        }
    }

    @Test
    void getModelCompletion_shouldReturnExistenceStatus() {
        UUID modelId = UUID.randomUUID();
        when(modelService.existsById(modelId)).thenReturn(true);

        ResponseEntity<CustomResponse<Boolean>> response = controller.getModelCompletion(modelId);

        verify(modelService).existsById(modelId);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Model completion is found");
        assertThat(response.getBody().getData()).isTrue();
    }
}
