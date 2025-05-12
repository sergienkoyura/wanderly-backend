package com.wanderly.userservice.service;

import java.util.UUID;

public interface UserARModelCompletionService {
    void save(UUID userId, UUID modelId, String cityName);

    Boolean existsById(UUID modelId);
}
