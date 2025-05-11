package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.ARModelDto;
import com.wanderly.geoservice.dto.ModelCompletionRequest;

import java.util.List;
import java.util.UUID;

public interface ARModelService {
    List<ARModelDto> findAllDtosByCityId(UUID userId, UUID cityId, Double userLatitude, Double userLongitude);

    void verifyModel(UUID userId, ModelCompletionRequest request);
}
