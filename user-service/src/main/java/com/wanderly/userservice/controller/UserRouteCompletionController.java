package com.wanderly.userservice.controller;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import com.wanderly.userservice.dto.UserRouteCompletionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user/completions")
@RequiredArgsConstructor
public class UserRouteCompletionController {

    @PostMapping("/routes")
    public ResponseEntity<CustomResponse<?>> saveCompletionStep(@RequestHeader("Authorization") String token,
                                                                @Valid @RequestBody UserRouteCompletionDto userRouteCompletionDto) {
        UUID userId = JwtUtil.extractUserId(token);

        return ResponseEntity.ok(ResponseFactory.success("Status saved", null));
    }
}
