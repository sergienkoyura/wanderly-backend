package com.wanderly.authservice.service.impl;

import com.wanderly.authservice.entity.User;
import com.wanderly.authservice.enums.AuthorizationType;
import com.wanderly.authservice.exception.AccountNotFoundException;
import com.wanderly.authservice.repository.UserRepository;
import com.wanderly.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public User register(String email, String password, AuthorizationType type) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authorizationType(type)
                .build();

        return userRepository.save(user);
    }

    @Override
    public boolean isTakenByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase()).isPresent();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(AccountNotFoundException::new);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(AccountNotFoundException::new);
    }

    @Override
    public void updateLastLogoutAt(User user) {
        user.setLastLogoutAt(LocalDateTime.now().minusMinutes(1));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }
}
