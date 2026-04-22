package com.example.identityservice.utils;

import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityUtils {
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.secret-key}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Duration REFRESHABLE_DURATION;

    @NonFinal
    @Value("${jwt.accessible-duration}")
    protected Duration ACCESSIBLE_DURATION;

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkMatchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String generateToken(User user, Duration exp) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Set<String> authorities = new HashSet<>();

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                // Thêm tiền tố ROLE_ để Spring Security hiểu được đây là Role
                authorities.add("ROLE_" + role.getName().name());

                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        authorities.add(permission.getName());
                    });
                }
            });
        }

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("MINI_FLASH_SALE")
                .issueTime(new Date())
                .expirationTime(Date
                        .from(Instant
                                .now()
                                .plus(exp)
                        )
                )
                .claim("userId", user.getId())
                .claim("scope", String.join("", authorities))
                .build();

        Payload payload = new Payload(claims.toJSONObject());

        JWSObject jws = new JWSObject(header, payload);

        try {
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            jws.sign(new MACSigner(keyBytes));
            return jws.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot create token", e);
        }
    }

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESSIBLE_DURATION);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESHABLE_DURATION); // 7 days
    }

    public SignedJWT verifyToken(String token) {
        try {
            // 1. Kiểm tra chữ ký (Signature)
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            JWSVerifier verifier = new MACVerifier(keyBytes);
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Xác minh xem token có được ký bởi đúng Secret Key của mình không
            boolean verified = signedJWT.verify(verifier);

            // 2. Kiểm tra thời hạn (Expiration)
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            if (!(verified && expiryTime.after(new Date()))) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            return signedJWT;
        } catch (JOSEException | ParseException e) {
            log.error("Token verification failed: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

    }
}
