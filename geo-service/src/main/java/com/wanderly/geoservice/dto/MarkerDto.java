package com.wanderly.geoservice.dto;

import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

//todo: validation
@Getter
@Setter
public class MarkerDto {
    private UUID id;
    private Double latitude;
    private Double longitude;
    private String name;
    private MarkerTag tag;
    private MarkerCategory category;
    private Integer orderIndex;
    private Integer stayingTime;
    private Double rating;
}
