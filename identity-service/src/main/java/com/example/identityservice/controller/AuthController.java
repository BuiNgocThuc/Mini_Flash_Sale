package com.example.identityservice.controller;

import com.example.identityservice.dto.request.auth.AuthRequest;
import com.example.identityservice.dto.request.auth.IntrospectRequest;
import com.example.identityservice.dto.request.user.UserCreationRequest;
import com.example.identityservice.dto.response.APIResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.dto.response.auth.AuthResponse;
import com.example.identityservice.dto.response.user.UserResponse;
import com.example.identityservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/register")
    public APIResponse<UserResponse> register(@RequestBody @Valid UserCreationRequest request) {
        return APIResponse.<UserResponse>builder()
                .result(authService.register(request))
                .message("Đăng ký tài khoản thành công")
                .build();

    }

    @PostMapping("/login")
    public APIResponse<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        var result = authService.authenticate(request);
        return APIResponse.<AuthResponse>builder()
                .result(result)
                .message("Đăng nhập thành công!")
                .build();
    }

    @PostMapping("/introspect")
    public APIResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        var result = authService.introspect(request);
        return APIResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

}
