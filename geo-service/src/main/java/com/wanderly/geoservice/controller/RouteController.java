package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.geoservice.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/geo")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @GetMapping("/routes/{cityId}")
    public ResponseEntity<CustomResponse<?>> generateMarkers(@RequestHeader("Authorization") String token,
                                                             @PathVariable(name = "cityId") UUID cityId) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("Routes found", routeService.findAllByCityIdAndUserId(cityId, userId)));
    }

    @GetMapping("/route/{cityId}")
    public ResponseEntity<CustomResponse<?>> generateRoute(@RequestHeader("Authorization") String token,
                                                           @PathVariable(name = "cityId") UUID cityId) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("Routes found", routeService.generateByCityIdAndUserId(cityId, userId)));
    }
}
