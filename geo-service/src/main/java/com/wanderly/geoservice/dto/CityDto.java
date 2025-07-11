package com.wanderly.geoservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CityDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;
    @NotNull
    private Integer osmId;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String details;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    @Size(min = 4, max = 4)
    private List<Double> boundingBox;
}
