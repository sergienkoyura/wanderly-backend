package com.wanderly.authservice.controller;

import com.wanderly.authservice.dto.request.LoginRequest;
import com.wanderly.authservice.dto.request.RefreshRequest;
import com.wanderly.authservice.dto.request.RegisterRequest;
import com.wanderly.authservice.dto.response.AuthorizationResponse;
import com.wanderly.authservice.entity.User;
import com.wanderly.authservice.enums.AuthorizationType;
import com.wanderly.authservice.enums.TokenType;
import com.wanderly.authservice.exception.*;
import com.wanderly.authservice.kafka.VerificationEmailProducer;
import com.wanderly.authservice.service.RedisService;
import com.wanderly.authservice.service.TokenService;
import com.wanderly.authservice.service.UserService;
import com.wanderly.authservice.util.CodeGeneratorUtil;
import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.dto.VerificationEmailMessage;
import com.wanderly.common.util.JwtUtil;
import com.wanderly.common.util.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RedisService redisService;
    private final VerificationEmailProducer verificationEmailProducer;
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<CustomResponse<?>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.isTakenByEmail(registerRequest.email())) {
            throw new TakenEmailException();
        }

        if (redisService.isEmailBlocked(registerRequest.email())) {
            throw new EmailRequestRateLimitException();
        }

        redisService.blockEmail(registerRequest.email());

        String verificationCode = CodeGeneratorUtil.generateVerificationCode();
        redisService.saveVerificationCode(registerRequest.email(), verificationCode);

        verificationEmailProducer.sendVerificationEmail(new VerificationEmailMessage(registerRequest.email(), verificationCode));

        return ResponseEntity.ok(ResponseFactory.success("Verification email sent", null));
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<CustomResponse<AuthorizationResponse>> verifyRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        String storedCode = redisService.getVerificationCode(registerRequest.email());

        if (storedCode == null) {
            throw new VerificationCodeExpiredException();
        }

        if (!storedCode.equals(registerRequest.code())) {
            throw new InvalidVerificationCodeException();
        }

        // Here user can now proceed to full registration or be activated
        redisService.deleteVerificationCode(registerRequest.email());

        User savedUser = userService.register(registerRequest.email(), registerRequest.password(), AuthorizationType.PLAIN);

        String accessToken = tokenService.generateToken(savedUser.getId(), TokenType.ACCESS);
        String refreshToken = tokenService.generateToken(savedUser.getId(), TokenType.REFRESH);

        return ResponseEntity.ok(ResponseFactory.success("Email verified successfully", new AuthorizationResponse(accessToken, refreshToken)));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        User authorizedUser = (User) authentication.getPrincipal();

        String accessToken = tokenService.generateToken(authorizedUser.getId(), TokenType.ACCESS);
        String refreshToken = tokenService.generateToken(authorizedUser.getId(), TokenType.REFRESH);

        return ResponseEntity.ok(ResponseFactory.success("Login successful", new AuthorizationResponse(accessToken, refreshToken)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<CustomResponse<?>> refreshToken(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();

        UUID userId = tokenService.extractUserId(refreshToken);

        if (userId == null || tokenService.isTokenExpired(refreshToken)) {
            throw new InvalidTokenException();
        }

        User user = userService.findById(userId);

        if (user.getLastLogoutAt() != null) {
            Instant tokenIssuedAt = tokenService.extractIssuedAt(refreshToken)
                    .plus(3, ChronoUnit.HOURS);
            Instant lastLogoutAt = user.getLastLogoutAt()
                    .atZone(ZoneOffset.UTC)
                    .toInstant();

            if (tokenIssuedAt.isBefore(lastLogoutAt)) {
                throw new InvalidTokenException(); // token blacklisted
            }
        }

        log.info("Refreshing by userId: {}", userId);

        userService.updateLastLogoutAt(user);

        String newAccessToken = tokenService.generateToken(user.getId(), TokenType.ACCESS);
        String newRefreshToken = tokenService.generateToken(user.getId(), TokenType.REFRESH);

        return ResponseEntity.ok(ResponseFactory.success("Token refreshed successfully", new AuthorizationResponse(newAccessToken, newRefreshToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<CustomResponse<?>> logout(@RequestHeader("Authorization") String token) {
        UUID userId = JwtUtil.extractUserId(token);
        User user = userService.findById(userId);
        userService.updateLastLogoutAt(user);
        return ResponseEntity.ok(ResponseFactory.success("Logout successful", null));
    }
}
