package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.userservice.dto.UserRouteCompletionDto;
import com.wanderly.userservice.service.UserARModelCompletionService;
import com.wanderly.userservice.service.UserRouteCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user/completions")
@RequiredArgsConstructor
public class CompletionController {
    private final UserRouteCompletionService userRouteCompletionService;
    private final UserARModelCompletionService userArModelCompletionService;

    @PostMapping("/routes")
    public ResponseEntity<CustomResponse<?>> saveRouteCompletionStep(@RequestHeader("Authorization") String token,
                                                                     @Valid @RequestBody UserRouteCompletionDto userRouteCompletionDto) {
        UUID userId = JwtUtil.extractUserId(token);
        userRouteCompletionService.save(userId, userRouteCompletionDto);
        return ResponseEntity.ok(ResponseFactory.success("Status saved", null));
    }

    @GetMapping("/routes/{routeId}")
    public ResponseEntity<CustomResponse<UserRouteCompletionDto>> getRouteCompletion(@RequestHeader("Authorization") String token,
                                                                                     @PathVariable(name = "routeId") UUID routeId) {
        UUID userId = JwtUtil.extractUserId(token);

        return ResponseEntity.ok(ResponseFactory.success("Completion for the route is found", userRouteCompletionService.findByRouteId(userId, routeId)));
    }


    @GetMapping("/ar-models/{modelId}")
    public ResponseEntity<CustomResponse<?>> getModelCompletion(@PathVariable(name = "modelId") UUID modelId) {
        return ResponseEntity.ok(ResponseFactory.success("Model completion is found", userArModelCompletionService.existsById(modelId)));
    }
}
