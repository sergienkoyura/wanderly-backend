package com.wanderly.geoservice.mapper;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.entity.RouteMarker;
import com.wanderly.geoservice.mapper.qualifier.RouteQualifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteQualifierTest {

    @Mock private MarkerMapper markerMapper;
    @InjectMocks private RouteQualifier routeQualifier;

    @Test
    void mapMarkers_MapsRouteMarkersToMarkerDtos() {
        // Arrange
        Marker marker = new Marker();
        marker.setId(UUID.randomUUID());

        RouteMarker routeMarker = RouteMarker.builder()
                .marker(marker)
                .orderIndex(1)
                .stayingTime(10)
                .build();

        MarkerDto mappedDto = new MarkerDto();
        mappedDto.setId(marker.getId());

        when(markerMapper.toDto(marker)).thenReturn(mappedDto);

        // Act
        List<MarkerDto> result = routeQualifier.mapMarkers(List.of(routeMarker));

        // Assert
        assertThat(result).hasSize(1);

        MarkerDto dto = result.getFirst();
        assertThat(dto.getId()).isEqualTo(marker.getId());
        assertThat(dto.getOrderIndex()).isEqualTo(1);
        assertThat(dto.getStayingTime()).isEqualTo(10);

        verify(markerMapper).toDto(marker);
    }
}
