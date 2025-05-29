package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.geoservice.dto.ARModelDto;
import com.wanderly.geoservice.dto.ModelCompletionRequest;
import com.wanderly.geoservice.service.ARModelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ARModelControllerTest {

    @Mock private ARModelService arModelService;
    @InjectMocks private ARModelController arModelController;

    @Test
    void generateModels_ReturnsMarkers_WhenTokenAndCityIdValid() {
        // Arrange
        String token = "Bearer mock-token";
        UUID cityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Double lat = 49.84;
        Double lng = 24.03;

        ARModelDto model1 = new ARModelDto();
        model1.setId(UUID.randomUUID());
        model1.setLatitude(lat);
        model1.setLongitude(lng);
        model1.setCode(111);

        List<ARModelDto> expectedModels = List.of(model1);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);
            when(arModelService.findAllDtosByCityId(userId, cityId, lat, lng)).thenReturn(expectedModels);

            // Act
            ResponseEntity<CustomResponse<List<ARModelDto>>> response = arModelController.generateModels(token, cityId, lat, lng);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("Markers found");

            List<ARModelDto> listResponse = response.getBody().getData();
            assertThat(listResponse).hasSize(1);
            assertThat(listResponse.getFirst().getCode()).isEqualTo(111);
        }
    }

    @Test
    void verifyModel_ReturnsOk_WhenVerificationSucceeds() {
        // Arrange
        String token = "Bearer mock-token";
        UUID userId = UUID.randomUUID();
        ModelCompletionRequest request = new ModelCompletionRequest(UUID.randomUUID(), 111);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.extractUserId(token)).thenReturn(userId);

            // No exception thrown = success
            doNothing().when(arModelService).verifyModel(userId, request);

            // Act
            ResponseEntity<CustomResponse<?>> response = arModelController.verifyModel(token, request);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("Model verified");
            assertThat(response.getBody().getData()).isNull();
        }
    }

}
