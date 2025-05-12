package com.wanderly.userservice.service.impl;

import com.wanderly.userservice.dto.UserRouteCompletionDto;
import com.wanderly.userservice.entity.UserRouteCompletion;
import com.wanderly.userservice.enums.RouteStatus;
import com.wanderly.userservice.exception.UserRouteCompletedException;
import com.wanderly.userservice.exception.UserRouteCompletionNotFound;
import com.wanderly.userservice.mapper.UserRouteCompletionMapper;
import com.wanderly.userservice.mapper.UserRouteCompletionMapperImpl;
import com.wanderly.userservice.repository.UserRouteCompletionRepository;
import com.wanderly.userservice.service.UserRouteCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRouteCompletionServiceImpl implements UserRouteCompletionService {
    private final UserRouteCompletionRepository userRouteCompletionRepository;
    private final UserRouteCompletionMapper userRouteCompletionMapper;


    @Override
    public void save(UUID userId, UserRouteCompletionDto userRouteCompletionDto) {
        UserRouteCompletion savedByRoute = userRouteCompletionRepository.findByRouteIdAndUserId(userRouteCompletionDto.getRouteId(), userId)
                .orElse(new UserRouteCompletion());

        if (savedByRoute.getStatus() != null && savedByRoute.getStatus().equals(RouteStatus.DONE)) {
            throw new UserRouteCompletedException();
        }

        savedByRoute.setUserId(userId);
        savedByRoute.setStep(userRouteCompletionDto.getStep());
        savedByRoute.setStatus(userRouteCompletionDto.getStatus());
        savedByRoute.setCityName(userRouteCompletionDto.getCityName());
        savedByRoute.setRouteId(userRouteCompletionDto.getRouteId());
        userRouteCompletionRepository.save(savedByRoute);
    }

    @Override
    public UserRouteCompletionDto findByRouteId(UUID userId, UUID routeId) {
        UserRouteCompletion foundCompletion = userRouteCompletionRepository.findByRouteIdAndUserId(routeId, userId)
                .orElseThrow(UserRouteCompletionNotFound::new);

        return userRouteCompletionMapper.toDto(foundCompletion);
    }

    @Override
    @Transactional
    public void eraseProgressByRouteId(UUID routeId) {
        userRouteCompletionRepository.deleteByRouteId(routeId);
    }
}
