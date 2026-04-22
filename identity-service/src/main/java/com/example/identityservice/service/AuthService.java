package com.example.identityservice.service;


import com.example.identityservice.dto.request.auth.AuthRequest;
import com.example.identityservice.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest request);


}
