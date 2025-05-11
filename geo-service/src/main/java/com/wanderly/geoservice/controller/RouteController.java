package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.geoservice.dto.BranchRequest;
import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.service.RouteService;
import jakarta.validation.Valid;
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

    @GetMapping("/routes/generate/{cityId}")
    public ResponseEntity<CustomResponse<?>> generateRoute(@RequestHeader("Authorization") String token,
                                                           @PathVariable(name = "cityId") UUID cityId) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("Routes found", routeService.generateByCityIdAndUserId(cityId, userId)));
    }

    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<CustomResponse<?>> deleteRoute(@RequestHeader("Authorization") String token,
                                                         @PathVariable(name = "routeId") UUID routeId) {
        UUID userId = JwtUtil.extractUserId(token);
        routeService.deleteByRouteIdAndUserId(routeId, userId);
        return ResponseEntity.ok(ResponseFactory.success("Route is deleted", null));
    }

    @PostMapping("/routes")
    public ResponseEntity<CustomResponse<RouteDto>> saveMarkers(@RequestHeader("Authorization") String token,
                                                                @Valid @RequestBody RouteDto routeDto) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("Route is saved", routeService.save(userId, routeDto)));
    }

    @PostMapping("/routes/branch")
    public ResponseEntity<CustomResponse<RouteDto>> saveMarkers(@RequestHeader("Authorization") String token,
                                                                @Valid @RequestBody BranchRequest branchRequest) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("Route is saved", routeService.branch(userId, branchRequest)));
    }
}
