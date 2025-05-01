package com.wanderly.authservice.service;

import com.wanderly.authservice.entity.User;
import com.wanderly.authservice.enums.AuthorizationType;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(String email, String password, AuthorizationType type);

    boolean isTakenByEmail(String email);

    User findByEmail(String email);

    void updateLastLogoutAt(User user);
}
