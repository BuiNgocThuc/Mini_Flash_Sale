package com.example.identityservice.service;


import com.example.identityservice.dto.request.auth.AuthRequest;
import com.example.identityservice.dto.request.user.UserCreationRequest;
import com.example.identityservice.dto.response.AuthResponse;
import com.example.identityservice.dto.response.UserResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest request);
    UserResponse register(UserCreationRequest request);



}
