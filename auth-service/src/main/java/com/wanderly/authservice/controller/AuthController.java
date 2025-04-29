package com.wanderly.authservice.controller;

import com.wanderly.authservice.dto.LoginRequest;
import com.wanderly.authservice.dto.RefreshRequest;
import com.wanderly.authservice.dto.RegisterRequest;
import com.wanderly.authservice.entity.User;
import com.wanderly.authservice.enums.AuthorizationType;
import com.wanderly.authservice.enums.TokenType;
import com.wanderly.authservice.exception.*;
import com.wanderly.authservice.kafka.VerificationEmailProducer;
import com.wanderly.authservice.service.RedisService;
import com.wanderly.authservice.service.TokenService;
import com.wanderly.authservice.service.UserService;
import com.wanderly.authservice.util.CodeGeneratorUtil;
import com.wanderly.common.dto.AuthorizationResponse;
import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.dto.VerificationEmailMessage;
import com.wanderly.common.util.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneOffset;

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
    public ResponseEntity<CustomResponse<?>> verifyRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        String storedCode = redisService.getVerificationCode(registerRequest.email());

        if (storedCode == null) {
            throw new VerificationCodeExpiredException();
        }

        if (!storedCode.equals(registerRequest.code())) {
            throw new InvalidVerificationCodeException();
        }

        // Here user can now proceed to full registration or be activated
        redisService.deleteVerificationCode(registerRequest.email());

        userService.register(registerRequest.email(), registerRequest.password(), AuthorizationType.PLAIN);

        String accessToken = tokenService.generateToken(registerRequest.email(), TokenType.ACCESS);
        String refreshToken = tokenService.generateToken(registerRequest.email(), TokenType.REFRESH);

        return ResponseEntity.ok(ResponseFactory.success("Email verified successfully", new AuthorizationResponse(accessToken, refreshToken)));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication ignoreAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        String accessToken = tokenService.generateToken(loginRequest.email(), TokenType.ACCESS);
        String refreshToken = tokenService.generateToken(loginRequest.email(), TokenType.REFRESH);

        return ResponseEntity.ok(ResponseFactory.success("Login successful", new AuthorizationResponse(accessToken, refreshToken)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<CustomResponse<?>> refreshToken(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();

        String email = tokenService.extractUsername(refreshToken);

        if (email == null || tokenService.isTokenExpired(refreshToken)) {
            throw new InvalidTokenException();
        }

        User user = userService.findByEmail(email);

        Instant tokenIssuedAt = tokenService.extractIssuedAt(refreshToken);
        if (user.getLastLogoutAt() != null && tokenIssuedAt.isBefore(user.getLastLogoutAt().toInstant(ZoneOffset.UTC))) {
            throw new InvalidTokenException(); // token blacklisted
        }

        userService.updateLastLogoutAt(user);

        String newAccessToken = tokenService.generateToken(email, TokenType.ACCESS);
        String newRefreshToken = tokenService.generateToken(email, TokenType.REFRESH);

        return ResponseEntity.ok(ResponseFactory.success("Token refreshed successfully", new AuthorizationResponse(newAccessToken, newRefreshToken)));
    }

    // @PostMapping("/logout")
}
