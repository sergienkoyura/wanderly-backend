package com.wanderly.userservice.service;

import com.wanderly.userservice.entity.UserARModelCompletion;
import com.wanderly.userservice.repository.ARModelCompletionRepository;
import com.wanderly.userservice.service.impl.UserARModelCompletionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserARModelCompletionServiceTest {

    @Mock private ARModelCompletionRepository arModelCompletionRepository;
    @InjectMocks private UserARModelCompletionServiceImpl userARModelCompletionService;

    @Test
    void save_shouldCreateNewIfNotExists() {
        UUID userId = UUID.randomUUID();
        UUID modelId = UUID.randomUUID();
        String cityName = "Kyiv";

        when(arModelCompletionRepository.findByUserIdAndModelId(userId, modelId))
                .thenReturn(Optional.empty());

        userARModelCompletionService.save(userId, modelId, cityName);

        ArgumentCaptor<UserARModelCompletion> captor = ArgumentCaptor.forClass(UserARModelCompletion.class);
        verify(arModelCompletionRepository).save(captor.capture());

        UserARModelCompletion saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getModelId()).isEqualTo(modelId);
        assertThat(saved.getCityName()).isEqualTo(cityName);
    }

    @Test
    void save_shouldUpdateIfExists() {
        UUID userId = UUID.randomUUID();
        UUID modelId = UUID.randomUUID();
        String cityName = "Lviv";

        UserARModelCompletion existing = new UserARModelCompletion();
        existing.setCityName("Old");

        when(arModelCompletionRepository.findByUserIdAndModelId(userId, modelId))
                .thenReturn(Optional.of(existing));

        userARModelCompletionService.save(userId, modelId, cityName);

        assertThat(existing.getCityName()).isEqualTo(cityName);
        verify(arModelCompletionRepository).save(existing);
    }

    @Test
    void existsById_shouldReturnTrueIfPresent() {
        UUID modelId = UUID.randomUUID();
        when(arModelCompletionRepository.findByModelId(modelId))
                .thenReturn(Optional.of(new UserARModelCompletion()));

        boolean exists = userARModelCompletionService.existsById(modelId);
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseIfMissing() {
        UUID modelId = UUID.randomUUID();
        when(arModelCompletionRepository.findByModelId(modelId))
                .thenReturn(Optional.empty());

        boolean exists = userARModelCompletionService.existsById(modelId);
        assertThat(exists).isFalse();
    }
}
