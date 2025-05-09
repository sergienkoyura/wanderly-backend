package com.wanderly.userservice.service.impl;

import com.wanderly.userservice.dto.UserRouteCompletionDto;
import com.wanderly.userservice.entity.UserRouteCompletion;
import com.wanderly.userservice.mapper.UserRouteCompletionMapper;
import com.wanderly.userservice.mapper.UserRouteCompletionMapperImpl;
import com.wanderly.userservice.repository.UserRouteCompletionRepository;
import com.wanderly.userservice.service.UserRouteCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRouteCompletionServiceImpl implements UserRouteCompletionService {
    private final UserRouteCompletionRepository userRouteCompletionRepository;
    private final UserRouteCompletionMapper userRouteCompletionMapper; // todo delete if not used


    @Override
    public void save(UUID userId, UserRouteCompletionDto userRouteCompletionDto) {
        UserRouteCompletion savedByRoute = userRouteCompletionRepository.findByRouteId(userRouteCompletionDto.getRouteId())
                .orElse(new UserRouteCompletion());

        savedByRoute.setUserId(userId);
        savedByRoute.setStep(userRouteCompletionDto.getStep());
        savedByRoute.setStatus(userRouteCompletionDto.getStatus());
        savedByRoute.setRouteId(userRouteCompletionDto.getRouteId());
        userRouteCompletionRepository.save(savedByRoute);
    }
}
