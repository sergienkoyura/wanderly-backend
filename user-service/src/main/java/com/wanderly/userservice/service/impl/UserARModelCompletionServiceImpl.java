package com.wanderly.userservice.service.impl;

import com.wanderly.userservice.entity.UserARModelCompletion;
import com.wanderly.userservice.repository.ARModelCompletionRepository;
import com.wanderly.userservice.service.UserARModelCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserARModelCompletionServiceImpl implements UserARModelCompletionService {
    private final ARModelCompletionRepository arModelCompletionRepository;

    @Override
    public void save(UUID userId, UUID modelId, String cityName) {
        UserARModelCompletion userARModelCompletion = arModelCompletionRepository.findByUserIdAndModelId(userId, modelId)
                .orElse(new UserARModelCompletion());

        userARModelCompletion.setUserId(userId);
        userARModelCompletion.setModelId(modelId);
        userARModelCompletion.setCityName(cityName);
        arModelCompletionRepository.save(userARModelCompletion);
    }

    @Override
    public Boolean existsById(UUID modelId) {
        return arModelCompletionRepository.findByModelId(modelId).isPresent();
    }
}
