package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.service.MarkerService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarkerControllerTest {

    @Mock private MarkerService markerService;
    @InjectMocks private MarkerController markerController;

    @Test
    void generateMarkers_ReturnsMarkers_WhenCityIdValid() {
        // Arrange
        UUID cityId = UUID.randomUUID();

        MarkerDto marker1 = new MarkerDto();
        MarkerDto marker2 = new MarkerDto();
        List<MarkerDto> markers = List.of(marker1, marker2);

        when(markerService.findAllDtosByCityId(cityId)).thenReturn(markers);

        // Act
        ResponseEntity<CustomResponse<List<MarkerDto>>> response = markerController.generateMarkers(cityId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Markers found");
        assertThat(response.getBody().getData()).hasSize(2);
    }
}
