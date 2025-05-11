package com.wanderly.geoservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BranchRequest(@NotNull UUID routeId, @NotNull @Min(0) Integer markerIndex) {
}
