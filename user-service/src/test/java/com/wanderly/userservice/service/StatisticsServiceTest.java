package com.wanderly.userservice.service;

import com.wanderly.userservice.dto.CityStatisticsDto;
import com.wanderly.userservice.dto.StatisticsDto;
import com.wanderly.userservice.repository.ARModelCompletionRepository;
import com.wanderly.userservice.repository.UserRouteCompletionRepository;
import com.wanderly.userservice.service.impl.StatisticsServiceImpl;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock private ARModelCompletionRepository arModelCompletionRepository;
    @Mock private UserRouteCompletionRepository userRouteCompletionRepository;
    @InjectMocks private StatisticsServiceImpl statisticsService;

    @Test
    void getStatistics_returnsCorrectAggregatedResult() {
        UUID userId = UUID.randomUUID();

        when(arModelCompletionRepository.countCompletedARModelsByUserId(userId)).thenReturn(5);
        when(userRouteCompletionRepository.countCompletedRoutesByUserId(userId)).thenReturn(10);

        Tuple kyivRouteTuple = mock(Tuple.class);
        when(kyivRouteTuple.get("cityName", String.class)).thenReturn("Kyiv");
        when(kyivRouteTuple.get("completedRoutes", Long.class)).thenReturn(7L);
        when(kyivRouteTuple.get("inProgressRoutes", Long.class)).thenReturn(2L);

        Tuple lvivRouteTuple = mock(Tuple.class);
        when(lvivRouteTuple.get("cityName", String.class)).thenReturn("Lviv");
        when(lvivRouteTuple.get("completedRoutes", Long.class)).thenReturn(3L);
        when(lvivRouteTuple.get("inProgressRoutes", Long.class)).thenReturn(1L);

        when(userRouteCompletionRepository.getRouteStatsPerCity(userId)).thenReturn(List.of(kyivRouteTuple, lvivRouteTuple));

        Tuple kyivARModelTuple = mock(Tuple.class);
        when(kyivARModelTuple.get("cityName", String.class)).thenReturn("Kyiv");
        when(kyivARModelTuple.get("completedARModels", Long.class)).thenReturn(2L);

        Tuple lvivARModelTuple = mock(Tuple.class);
        when(lvivARModelTuple.get("cityName", String.class)).thenReturn("Lviv");
        when(lvivARModelTuple.get("completedARModels", Long.class)).thenReturn(3L);

        when(arModelCompletionRepository.getARModelStatsPerCity(userId)).thenReturn(List.of(kyivARModelTuple, lvivARModelTuple));

        StatisticsDto result = statisticsService.getStatistics(userId);

        assertThat(result.getTotalCompletedARModels()).isEqualTo(5L);
        assertThat(result.getTotalCompletedRoutes()).isEqualTo(10L);
        assertThat(result.getCities()).hasSize(2);

        CityStatisticsDto first = result.getCities().get(0);
        CityStatisticsDto second = result.getCities().get(1);

        assertThat(first.getName()).isEqualTo("Kyiv");
        assertThat(first.getCompletedRoutes()).isEqualTo(7L);
        assertThat(first.getInProgressRoutes()).isEqualTo(2L);
        assertThat(first.getCompletedARModels()).isEqualTo(2L);

        assertThat(second.getName()).isEqualTo("Lviv");
        assertThat(second.getCompletedRoutes()).isEqualTo(3L);
        assertThat(second.getInProgressRoutes()).isEqualTo(1L);
        assertThat(second.getCompletedARModels()).isEqualTo(3L);
    }
}
