package com.wanderly.userservice.service;

import com.wanderly.userservice.dto.StatisticsDto;

import java.util.UUID;

public interface StatisticsService {
    StatisticsDto getStatistics(UUID userId);
}
