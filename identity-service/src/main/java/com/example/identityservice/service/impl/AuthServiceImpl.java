package com.example.identityservice.service.impl;


import com.example.identityservice.dto.request.auth.AuthRequest;
import com.example.identityservice.dto.request.auth.IntrospectRequest;
import com.example.identityservice.dto.request.auth.LogoutRequest;
import com.example.identityservice.dto.request.auth.RefreshTokenRequest;
import com.example.identityservice.dto.request.user.UserCreationRequest;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.dto.response.auth.AuthResponse;
import com.example.identityservice.dto.response.user.UserResponse;
import com.example.identityservice.entity.Permission;
import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.enums.RoleType;
import com.example.identityservice.enums.UserStatus;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapping.UserMapper;
import com.example.identityservice.redis.RefreshToken;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.repository.redis.RefreshTokenRepository;
import com.example.identityservice.utils.SecurityUtils;
import com.example.identityservice.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    RefreshTokenRepository refreshTokenRepository;
    AuthenticationManager authenticationManager;
    SecurityUtils securityUtils;
    UserMapper userMapper;


    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!securityUtils.checkMatchPassword(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        String accessToken = securityUtils.generateAccessToken(user);
        String refreshToken = securityUtils.generateRefreshToken(user);

        // Lưu Refresh Token vào Redis
        refreshTokenRepository.save(RefreshToken.builder()
                .id(refreshToken)
                .userId(user.getId())
                .build());

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public UserResponse register(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(securityUtils.encryptPassword(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        user.setRoles(new HashSet<>(Set.of(userRole)));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    // --- LÀM MỚI TOKEN (Token Rotation) ---
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        var storedToken = refreshTokenRepository.findById(request.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        var user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xóa Token cũ ngay lập tức (Rotation)
        refreshTokenRepository.deleteById(storedToken.getId());

        String newAccessToken = securityUtils.generateAccessToken(user);
        String newRefreshToken = securityUtils.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .id(newRefreshToken)
                .userId(user.getId())
                .build());

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    // --- KIỂM TRA TOKEN ---
    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            securityUtils.verifyToken(request.getToken());
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    // --- ĐĂNG XUẤT ---
    @Override
    public void logout(LogoutRequest request) {
        refreshTokenRepository.deleteById(request.getRefreshToken());
    }

    // --- HELPER ---
    private AuthResponse buildAuthResponse(User user, String at, String rt) {
        return AuthResponse.builder()
                .accessToken(at)
                .refreshToken(rt)
                .isAuthenticated(true)
                .userSummary(AuthResponse.UserSummary.builder()
                        .username(user.getUsername())
                        .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                        .permissions(user.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(Permission::getName).collect(Collectors.toSet()))
                        .build())
                .build();
    }
}
