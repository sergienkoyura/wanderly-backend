package com.wanderly.geoservice.service;

import com.wanderly.common.dto.geo.CityLookupRequest;

public interface CityService {
    void save(CityLookupRequest cityLookupRequest);

}
