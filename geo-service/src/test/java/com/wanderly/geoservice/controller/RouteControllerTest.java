package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.geoservice.dto.BranchRequest;
import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    @Mock private RouteService routeService;
    @InjectMocks private RouteController routeController;

    @Test
    void generateMarkers_ReturnsRoutes() {
        UUID cityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<RouteDto> mockRoutes = List.of(new RouteDto());

        try (var jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId("token")).thenReturn(userId);
            when(routeService.findAllByCityIdAndUserId(cityId, userId)).thenReturn(mockRoutes);

            ResponseEntity<CustomResponse<?>> response = routeController.generateMarkers("token", cityId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("Routes found");
            assertThat(response.getBody().getData()).isEqualTo(mockRoutes);
        }
    }

    @Test
    void generateRoute_ReturnsGeneratedRoutes() {
        UUID cityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RouteDto generated = new RouteDto();

        try (var jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId("token")).thenReturn(userId);
            when(routeService.generateByCityIdAndUserId(cityId, userId)).thenReturn(generated);

            ResponseEntity<CustomResponse<?>> response = routeController.generateRoute("token", cityId);

            assertThat(response.getBody().getMessage()).isEqualTo("Route is found");
            assertThat(response.getBody().getData()).isEqualTo(generated);
        }
    }

    @Test
    void deleteRoute_DeletesSuccessfully() {
        UUID routeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        try (var jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId("token")).thenReturn(userId);

            ResponseEntity<CustomResponse<?>> response = routeController.deleteRoute("token", routeId);

            verify(routeService).deleteByRouteIdAndUserId(routeId, userId);
            assertThat(response.getBody().getMessage()).isEqualTo("Route is deleted");
        }
    }

    @Test
    void saveMarkers_SavesRoute() {
        UUID userId = UUID.randomUUID();
        RouteDto request = new RouteDto();
        RouteDto saved = new RouteDto();

        try (var jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId("token")).thenReturn(userId);
            when(routeService.save(userId, request)).thenReturn(saved);

            ResponseEntity<CustomResponse<RouteDto>> response = routeController.saveMarkers("token", request);

            assertThat(response.getBody().getMessage()).isEqualTo("Route is saved");
            assertThat(response.getBody().getData()).isEqualTo(saved);
        }
    }

    @Test
    void branch_SavesBranchedRoute() {
        UUID userId = UUID.randomUUID();
        BranchRequest request = new BranchRequest(UUID.randomUUID(), 1);
        RouteDto saved = new RouteDto();

        try (var jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.extractUserId("token")).thenReturn(userId);
            when(routeService.branch(userId, request)).thenReturn(saved);

            ResponseEntity<CustomResponse<RouteDto>> response = routeController.saveMarkers("token", request);

            assertThat(response.getBody().getMessage()).isEqualTo("Route is saved");
            assertThat(response.getBody().getData()).isEqualTo(saved);
        }
    }
}
