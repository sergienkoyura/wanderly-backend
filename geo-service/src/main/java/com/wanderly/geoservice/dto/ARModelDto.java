package com.wanderly.geoservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ARModelDto {
    @NotNull
    private UUID id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double latitude;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double longitude;
    private Integer code;
}
