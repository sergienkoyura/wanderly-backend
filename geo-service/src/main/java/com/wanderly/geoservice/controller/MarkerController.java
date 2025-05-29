package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/geo")
@RequiredArgsConstructor
public class MarkerController {
    private final MarkerService markerService;

    @GetMapping("/markers/{cityId}")
    public ResponseEntity<CustomResponse<List<MarkerDto>>> generateMarkers(@PathVariable(name = "cityId") UUID cityId) {
        return ResponseEntity.ok(ResponseFactory.success("Markers found", markerService.findAllDtosByCityId(cityId)));
    }
}
