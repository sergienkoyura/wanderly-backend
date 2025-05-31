package com.wanderly.authservice.controller;

import com.wanderly.authservice.dto.request.LoginRequest;
import com.wanderly.authservice.dto.request.RefreshRequest;
import com.wanderly.authservice.dto.request.RegisterRequest;
import com.wanderly.authservice.dto.response.AuthorizationResponse;
import com.wanderly.authservice.entity.User;
import com.wanderly.authservice.enums.AuthorizationType;
import com.wanderly.authservice.enums.TokenType;
import com.wanderly.authservice.exception.EmailRequestRateLimitException;
import com.wanderly.authservice.exception.InvalidVerificationCodeException;
import com.wanderly.authservice.exception.TakenEmailException;
import com.wanderly.authservice.exception.VerificationCodeExpiredException;
import com.wanderly.authservice.kafka.VerificationEmailProducer;
import com.wanderly.authservice.service.RedisService;
import com.wanderly.authservice.service.TokenService;
import com.wanderly.authservice.service.UserService;
import com.wanderly.authservice.util.CodeGeneratorUtil;
import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private UserService userService;
    @Mock private RedisService redisService;
    @Mock private TokenService tokenService;
    @Mock private VerificationEmailProducer verificationEmailProducer;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private AuthController authController;

    @Test
    void fullRegistrationFlow_Succeeds_WhenValidVerificationCode() {
        // Arrange
        String email = "user@example.com";
        String password = "password123";
        String verificationCode = "123456";

        RegisterRequest request = new RegisterRequest(email, password, verificationCode);
        User mockUser = User.builder().id(UUID.randomUUID()).build();

        // Registration mocks
        when(userService.isTakenByEmail(email)).thenReturn(false);
        when(redisService.isEmailBlocked(email)).thenReturn(false);
        doNothing().when(redisService).blockEmail(email);
        doNothing().when(redisService).saveVerificationCode(email, verificationCode);
        doNothing().when(verificationEmailProducer).sendVerificationEmail(any());

        // Verification mocks
        when(redisService.getVerificationCode(email)).thenReturn(verificationCode);
        when(userService.register(email, password, AuthorizationType.PLAIN)).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser.getId(), TokenType.ACCESS)).thenReturn("access-token");
        when(tokenService.generateToken(mockUser.getId(), TokenType.REFRESH)).thenReturn("refresh-token");

        try (MockedStatic<CodeGeneratorUtil> mocked = mockStatic(CodeGeneratorUtil.class)) {
            mocked.when(CodeGeneratorUtil::generateVerificationCode).thenReturn(verificationCode);

            ResponseEntity<CustomResponse<?>> registerResponse = authController.registerUser(request);
            assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(registerResponse.getBody()).isNotNull();
            assertThat(registerResponse.getBody().getMessage()).isEqualTo("Verification email sent");

            ResponseEntity<CustomResponse<AuthorizationResponse>> verifyResponse = authController.verifyRegistration(request);
            assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(verifyResponse.getBody()).isNotNull();
            assertThat(verifyResponse.getBody().getMessage()).isEqualTo("Email verified successfully");

            AuthorizationResponse tokens = verifyResponse.getBody().getData();
            assertThat(tokens.accessToken()).isEqualTo("access-token");
            assertThat(tokens.refreshToken()).isEqualTo("refresh-token");

            // Verify
            verify(redisService).blockEmail(email);
            verify(redisService).saveVerificationCode(email, verificationCode);
            verify(verificationEmailProducer).sendVerificationEmail(any());
            verify(redisService).getVerificationCode(email);
            verify(redisService).deleteVerificationCode(email);
            verify(userService).register(email, password, AuthorizationType.PLAIN);
        }
    }

    @Test
    void registerUser_ThrowsTakenEmailException_WhenEmailAlreadyExists() {
        // Arrange
        String email = "user@example.com";
        RegisterRequest request = new RegisterRequest(email, "password", null);

        when(userService.isTakenByEmail(email)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> authController.registerUser(request))
                .isInstanceOf(TakenEmailException.class);
    }

    @Test
    void registerUser_ThrowsEmailRequestRateLimitException_WhenEmailIsBlocked() {
        // Arrange
        String email = "user@example.com";
        RegisterRequest request = new RegisterRequest(email, "password", null);

        when(userService.isTakenByEmail(email)).thenReturn(false);
        when(redisService.isEmailBlocked(email)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> authController.registerUser(request))
                .isInstanceOf(EmailRequestRateLimitException.class);
    }

    @Test
    void verifyRegistration_ThrowsVerificationCodeExpiredException_WhenCodeNotFound() {
        // Arrange
        String email = "user@example.com";
        RegisterRequest request = new RegisterRequest(email, "password", "123456");

        when(redisService.getVerificationCode(email)).thenReturn(null);

        // Act + Assert
        assertThatThrownBy(() -> authController.verifyRegistration(request))
                .isInstanceOf(VerificationCodeExpiredException.class);
    }

    @Test
    void verifyRegistration_ThrowsInvalidVerificationCodeException_WhenCodeDoesNotMatch() {
        // Arrange
        String email = "user@example.com";
        RegisterRequest request = new RegisterRequest(email, "password", "wrong-code");

        when(redisService.getVerificationCode(email)).thenReturn("correct-code");

        // Act + Assert
        assertThatThrownBy(() -> authController.verifyRegistration(request))
                .isInstanceOf(InvalidVerificationCodeException.class);
    }

    @Test
    void login_ReturnsTokens_WhenCredentialsAreValid() {
        // Arrange
        String email = "user@example.com";
        String password = "password123";
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();
        Authentication auth = mock(Authentication.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(userId, TokenType.ACCESS)).thenReturn("access-token");
        when(tokenService.generateToken(userId, TokenType.REFRESH)).thenReturn("refresh-token");


        // Act
        ResponseEntity<CustomResponse<?>> response = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        AuthorizationResponse tokens = (AuthorizationResponse) response.getBody().getData();
        assertThat(tokens.accessToken()).isEqualTo("access-token");
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void refreshToken_ReturnsNewTokens_WhenTokenIsValid() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();

        String refreshToken = "valid-refresh-token";
        RefreshRequest request = new RefreshRequest(refreshToken);

        when(tokenService.extractUserId(refreshToken)).thenReturn(userId);
        when(tokenService.isTokenExpired(refreshToken)).thenReturn(false);
        when(userService.findById(userId)).thenReturn(user);
        when(tokenService.generateToken(userId, TokenType.ACCESS)).thenReturn("new-access-token");
        when(tokenService.generateToken(userId, TokenType.REFRESH)).thenReturn("new-refresh-token");

        // Act
        ResponseEntity<CustomResponse<?>> response = authController.refreshToken(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        AuthorizationResponse tokens = (AuthorizationResponse) response.getBody().getData();
        assertThat(tokens.accessToken()).isEqualTo("new-access-token");
        assertThat(tokens.refreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void logout_Succeeds_WhenTokenIsValid() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String bearerToken = "Bearer mock-token";
        User user = User.builder().id(userId).build();

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.extractUserId(bearerToken)).thenReturn(userId);
            when(userService.findById(userId)).thenReturn(user);

            // Act
            ResponseEntity<CustomResponse<?>> response = authController.logout(bearerToken);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("Logout successful");

            verify(userService).updateLastLogoutAt(user);
        }
    }




//    @Test
//    void register_ReturnsConflict_WhenEmailTaken() {
//        when(userService.isTakenByEmail("user@example.com")).thenReturn(true);
//        ResponseEntity<UserDto> response = authController.register(new RegisterRequest("user@example.com", "password"));
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//    }
//
//    @Test
//    void login_ReturnsToken_WhenCredentialsValid() {
//        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
//        // when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
//        ResponseEntity<AuthResponse> response = authController.login(new LoginRequest("user@example.com", "password"));
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody().getToken()).isEqualTo("jwt-token");
//    }
//
//    @Test
//    void login_ReturnsUnauthorized_WhenCredentialsInvalid() {
//        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
//        ResponseEntity<AuthResponse> response = authController.login(new LoginRequest("user@example.com", "wrongpassword"));
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//    }

//    @Test
//    void me_ReturnsUserDto_WhenTokenIsValid() {
//        UserDto userDto = new UserDto();
//        when(userService.findDtoById(any(UUID.class))).thenReturn(userDto);
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn("user-id");
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        ResponseEntity<UserDto> response = userController.me();
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isEqualTo(userDto);
//    }
//
//    @Test
//    void me_ReturnsUnauthorized_WhenNoTokenProvided() {
//        SecurityContextHolder.clearContext();
//        ResponseEntity<UserDto> response = userController.me();
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//    }
}