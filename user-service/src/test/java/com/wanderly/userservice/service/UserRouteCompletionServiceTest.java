package com.wanderly.userservice.service;

import com.wanderly.userservice.dto.UserRouteCompletionDto;
import com.wanderly.userservice.entity.UserRouteCompletion;
import com.wanderly.userservice.enums.RouteStatus;
import com.wanderly.userservice.exception.UserRouteCompletedException;
import com.wanderly.userservice.exception.UserRouteCompletionNotFound;
import com.wanderly.userservice.mapper.UserRouteCompletionMapper;
import com.wanderly.userservice.repository.UserRouteCompletionRepository;
import com.wanderly.userservice.service.impl.UserRouteCompletionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRouteCompletionServiceTest {

    @Mock private UserRouteCompletionRepository userRouteCompletionRepository;
    @Mock private UserRouteCompletionMapper userRouteCompletionMapper;
    @InjectMocks private UserRouteCompletionServiceImpl userRouteCompletionService;

    @Test
    void save_shouldCreateNewCompletionWhenNoneExists() {
        UUID userId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();

        UserRouteCompletionDto dto = UserRouteCompletionDto.builder()
                .routeId(routeId)
                .step(3)
                .status(RouteStatus.IN_PROGRESS)
                .cityName("Kyiv")
                .build();

        when(userRouteCompletionRepository.findByRouteIdAndUserId(routeId, userId))
                .thenReturn(Optional.empty());

        userRouteCompletionService.save(userId, dto);

        ArgumentCaptor<UserRouteCompletion> captor = ArgumentCaptor.forClass(UserRouteCompletion.class);
        verify(userRouteCompletionRepository).save(captor.capture());

        UserRouteCompletion saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getStep()).isEqualTo(3);
        assertThat(saved.getStatus()).isEqualTo(RouteStatus.IN_PROGRESS);
        assertThat(saved.getCityName()).isEqualTo("Kyiv");
        assertThat(saved.getRouteId()).isEqualTo(routeId);
    }

    @Test
    void save_shouldThrowWhenStatusIsDone() {
        UUID userId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();

        UserRouteCompletion existing = new UserRouteCompletion();
        existing.setStatus(RouteStatus.DONE);

        UserRouteCompletionDto dto = UserRouteCompletionDto.builder()
                .routeId(routeId)
                .step(2)
                .status(RouteStatus.IN_PROGRESS)
                .build();

        when(userRouteCompletionRepository.findByRouteIdAndUserId(routeId, userId))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userRouteCompletionService.save(userId, dto))
                .isInstanceOf(UserRouteCompletedException.class);
    }

    @Test
    void findByRouteId_shouldReturnMappedDto() {
        UUID userId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();

        UserRouteCompletion entity = new UserRouteCompletion();
        UserRouteCompletionDto dto = new UserRouteCompletionDto();

        when(userRouteCompletionRepository.findByRouteIdAndUserId(routeId, userId))
                .thenReturn(Optional.of(entity));
        when(userRouteCompletionMapper.toDto(entity)).thenReturn(dto);

        UserRouteCompletionDto result = userRouteCompletionService.findByRouteId(userId, routeId);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void findByRouteId_shouldThrowIfNotFound() {
        UUID userId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();

        when(userRouteCompletionRepository.findByRouteIdAndUserId(routeId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRouteCompletionService.findByRouteId(userId, routeId))
                .isInstanceOf(UserRouteCompletionNotFound.class);
    }

    @Test
    void eraseProgressByRouteId_shouldDeleteRecord() {
        UUID routeId = UUID.randomUUID();

        userRouteCompletionService.eraseProgressByRouteId(routeId);

        verify(userRouteCompletionRepository).deleteByRouteId(routeId);
    }
}
