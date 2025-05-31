package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.ARModelDto;
import com.wanderly.geoservice.dto.ModelCompletionRequest;
import com.wanderly.geoservice.entity.ARModel;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.UserPreferences;
import com.wanderly.geoservice.exception.ARModelNotFoundException;
import com.wanderly.geoservice.exception.ARModelVerificationFailureException;
import com.wanderly.geoservice.kafka.UserARModelCompletionProducer;
import com.wanderly.geoservice.mapper.ARModelMapper;
import com.wanderly.geoservice.repository.ARModelRepository;
import com.wanderly.geoservice.service.impl.ARModelServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ARModelServiceTest {

    @Mock private ARModelRepository arModelRepository;
    @Mock private ARModelMapper arModelMapper;
    @Mock private UserPreferencesService userPreferencesService;
    @Mock private CityService cityService;
    @Mock private MarkerService markerService;
    @Mock private UserARModelCompletionProducer producer;
    @InjectMocks private ARModelServiceImpl arModelService;

    private final UUID userId = UUID.randomUUID();
    private final UUID cityId = UUID.randomUUID();

    @Test
    void findAllDtosByCityId_ReturnsCachedModelsIfExist() {
        List<ARModel> existing = List.of(new ARModel());
        List<ARModelDto> dtos = List.of(new ARModelDto());

        when(arModelRepository.findAllByCityIdAndUserId(cityId, userId)).thenReturn(existing);
        when(arModelMapper.toDtos(existing)).thenReturn(dtos);

        List<ARModelDto> result = arModelService.findAllDtosByCityId(userId, cityId, null, null);

        assertThat(result).isEqualTo(dtos);
        verify(arModelRepository).findAllByCityIdAndUserId(cityId, userId);
        verifyNoMoreInteractions(arModelRepository);
    }

    @Test
    void findAllDtosByCityId_GeneratesModelsIfEmptyAndCoordinatesPresent() {
        when(arModelRepository.findAllByCityIdAndUserId(cityId, userId)).thenReturn(new ArrayList<>());
        when(cityService.findById(cityId)).thenReturn(new City());
        when(markerService.findAllByCityIdAndCategoryNature(cityId)).thenReturn(Collections.emptyList());
        when(userPreferencesService.findByUserId(userId)).thenReturn(new UserPreferences());

        List<ARModel> modelsToSave = new ArrayList<>();
        when(arModelRepository.saveAll(anyList())).thenAnswer(invocation -> {
            modelsToSave.addAll(invocation.getArgument(0));
            return modelsToSave;
        });
        when(arModelMapper.toDtos(anyList())).thenReturn(List.of(new ARModelDto()));

        List<ARModelDto> result = arModelService.findAllDtosByCityId(userId, cityId, 1.0, 1.0);

        assertThat(result).hasSize(1);
        verify(arModelRepository).saveAll(anyList());
    }

    @Test
    void verifyModel_Succeeds_WhenCodeIsCorrect() {
        UUID modelId = UUID.randomUUID();
        ModelCompletionRequest request = new ModelCompletionRequest(modelId, 1234);

        City city = new City();
        city.setName("Kyiv");

        ARModel model = ARModel.builder()
                .cityId(cityId)
                .code(1234)
                .build();

        when(arModelRepository.findById(modelId)).thenReturn(Optional.of(model));
        when(cityService.findById(cityId)).thenReturn(city);

        arModelService.verifyModel(userId, request);

        verify(producer).sendCompletionMessage(any());
    }

    @Test
    void verifyModel_ThrowsException_WhenModelNotFound() {
        UUID modelId = UUID.randomUUID();
        when(arModelRepository.findById(modelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> arModelService.verifyModel(userId, new ModelCompletionRequest(modelId, 1234)))
                .isInstanceOf(ARModelNotFoundException.class);
    }

    @Test
    void verifyModel_ThrowsException_WhenCodeIsInvalid() {
        UUID modelId = UUID.randomUUID();
        ARModel model = ARModel.builder()
                .cityId(cityId)
                .code(1234)
                .build();

        when(arModelRepository.findById(modelId)).thenReturn(Optional.of(model));

        assertThatThrownBy(() -> arModelService.verifyModel(userId, new ModelCompletionRequest(modelId, 9999)))
                .isInstanceOf(ARModelVerificationFailureException.class);
    }
}
