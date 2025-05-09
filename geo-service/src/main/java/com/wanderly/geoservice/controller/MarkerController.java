package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.geoservice.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/geo")
@RequiredArgsConstructor
public class MarkerController {
    private final MarkerService markerService;

    @GetMapping("/markers/{cityId}")
    public ResponseEntity<CustomResponse<?>> generateMarkers(@PathVariable(name = "cityId") UUID cityId) {
        return ResponseEntity.ok(ResponseFactory.success("Markers found", markerService.findAllByCityId(cityId)));
    }
}
