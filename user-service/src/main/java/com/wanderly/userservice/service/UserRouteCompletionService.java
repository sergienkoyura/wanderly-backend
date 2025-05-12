package com.wanderly.userservice.service;

import com.wanderly.userservice.dto.UserRouteCompletionDto;

import java.util.UUID;

public interface UserRouteCompletionService {
    void save(UUID userId, UserRouteCompletionDto userRouteCompletionDto);

    UserRouteCompletionDto findByRouteId(UUID userId, UUID routeId);

    void eraseProgressByRouteId(UUID routeId);
}
