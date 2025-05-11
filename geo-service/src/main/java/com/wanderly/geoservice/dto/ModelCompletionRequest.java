package com.wanderly.geoservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ModelCompletionRequest(@NotNull UUID modelId, @NotNull Integer code) {
}
