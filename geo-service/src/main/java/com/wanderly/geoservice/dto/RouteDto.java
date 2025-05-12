package com.wanderly.geoservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wanderly.geoservice.enums.RouteCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RouteDto {
    @NotNull
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RouteCategory category;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer avgTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer avgStayingTime;

    @NotNull
    @Valid
    private List<MarkerDto> markers;
}
