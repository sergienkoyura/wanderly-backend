package com.wanderly.authservice.service;

import com.wanderly.authservice.dto.response.UserDto;
import com.wanderly.authservice.entity.User;
import com.wanderly.authservice.enums.AuthorizationType;
import com.wanderly.authservice.exception.AccountNotFoundException;
import com.wanderly.authservice.mapper.UserMapper;
import com.wanderly.authservice.repository.UserRepository;
import com.wanderly.authservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void register_SavesUserWithEncodedPassword() {
        // Arrange
        String email = "user@example.com";
        String rawPassword = "password";
        String encodedPassword = "encoded123";
        AuthorizationType type = AuthorizationType.PLAIN;

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User savedUser = userService.register(email, rawPassword, type);

        // Assert
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getAuthorizationType()).isEqualTo(type);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void isTakenByEmail_ReturnsTrue_WhenEmailExists() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new User()));
        boolean taken = userService.isTakenByEmail("user@example.com");
        assertThat(taken).isTrue();
    }

    @Test
    void isTakenByEmail_ReturnsFalse_WhenEmailNotExists() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        boolean taken = userService.isTakenByEmail("user@example.com");
        assertThat(taken).isFalse();
    }

    @Test
    void findByEmail_ReturnsUser_WhenExists() {
        User user = User.builder().email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertThat(userService.findByEmail("user@example.com")).isEqualTo(user);
    }

    @Test
    void findByEmail_ThrowsException_WhenNotExists() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByEmail("user@example.com"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void findById_ReturnsUser_WhenExists() {
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertThat(userService.findById(id)).isEqualTo(user);
    }

    @Test
    void findById_ThrowsException_WhenNotExists() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void findDtoById_ReturnsMappedDto() {
        String email = "test@test.com";
        User user = User.builder().email(email).build();
        UserDto dto = new UserDto();
        dto.setEmail(email);


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(dto);

        assertThat(userService.findDtoById(any())).isEqualTo(dto);
    }

    @Test
    void updateLastLogoutAt_SetsTimestampAndSaves() {
        User user = User.builder().email("user@example.com").build();
        userService.updateLastLogoutAt(user);
        assertThat(user.getLastLogoutAt()).isBeforeOrEqualTo(LocalDateTime.now());
        verify(userRepository).save(user);
    }

    @Test
    void loadUserByUsername_ReturnsUserDetails_WhenFound() {
        User user = User.builder().email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserDetails result = userService.loadUserByUsername("user@example.com");
        assertThat(result).isEqualTo(user);
    }

    @Test
    void loadUserByUsername_ThrowsException_WhenNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.loadUserByUsername("user@example.com"))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
