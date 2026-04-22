package com.example.identityservice.service.impl;


import com.example.identityservice.dto.request.auth.AuthRequest;
import com.example.identityservice.dto.response.AuthResponse;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.repository.redis.RefreshTokenRepository;
import com.example.identityservice.security.SecurityUtils;
import com.example.identityservice.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    RefreshTokenRepository refreshTokenRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    SecurityUtils securityUtils;


    @Override
    public AuthResponse authenticate(AuthRequest request) {
        return null;
    }
}
