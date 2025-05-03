package com.wanderly.common.dto.geo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityLookupResponse {
    private UUID preferencesId;
    private UUID cityId;
}
