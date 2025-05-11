package com.wanderly.geoservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.geoservice.dto.ModelCompletionRequest;
import com.wanderly.geoservice.service.ARModelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/geo")
@RequiredArgsConstructor
public class ARModelController {
    private final ARModelService arModelService;

    @GetMapping("/ar-models/{cityId}")
    public ResponseEntity<CustomResponse<?>> generateModels(@RequestHeader("Authorization") String token,
                                                            @PathVariable(name = "cityId") UUID cityId,
                                                            @RequestParam(name = "lat", required = false) Double lat,
                                                            @RequestParam(name = "lng", required = false) Double lng) {
        UUID userId = JwtUtil.extractUserId(token);
        return ResponseEntity.ok(ResponseFactory.success("Markers found", arModelService.findAllDtosByCityId(userId, cityId, lat, lng)));
    }

    @PostMapping("/ar-models/verify")
    public ResponseEntity<CustomResponse<?>> verifyModel(@RequestHeader("Authorization") String token,
                                                         @Valid @RequestBody ModelCompletionRequest request) {
        UUID userId = JwtUtil.extractUserId(token);
        arModelService.verifyModel(userId, request);
        return ResponseEntity.ok(ResponseFactory.success("Model verified", null));
    }
}
