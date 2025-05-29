package com.wanderly.geoservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class MarkerDto implements Serializable {
    @NotNull
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double latitude;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double longitude;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MarkerTag tag;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MarkerCategory category;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer orderIndex;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer stayingTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double rating;
}
