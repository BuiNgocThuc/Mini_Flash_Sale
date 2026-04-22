package com.example.identityservice.service;


import com.example.identityservice.dto.request.auth.AuthRequest;
import com.example.identityservice.dto.request.auth.IntrospectRequest;
import com.example.identityservice.dto.request.auth.LogoutRequest;
import com.example.identityservice.dto.request.auth.RefreshTokenRequest;
import com.example.identityservice.dto.request.user.UserCreationRequest;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.dto.response.auth.AuthResponse;
import com.example.identityservice.dto.response.user.UserResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest request);
    UserResponse register(UserCreationRequest request);
    UserResponse getMyInfo();
    AuthResponse refreshToken(RefreshTokenRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    void logout(LogoutRequest request);
}
