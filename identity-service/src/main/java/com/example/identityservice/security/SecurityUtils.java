package com.example.identityservice.security;

import com.example.identityservice.entity.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    protected int REFRESHABLE_DURATION;

    @NonFinal
    @Value("${jwt.accessible-duration}")
    protected int ACCESSIBLE_DURATION;

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkMatchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String generateToken(User user, long expMin) { // expiration minute
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Set<String> authorities = new HashSet<>();

        if(!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach( role -> {
                authorities.add(role.getName().name());

                role.getPermissions().forEach(permission -> {
                    authorities.add(permission.getName());
                });
            });
        }

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("MINI_FLASH_SALE")
                .issueTime(new Date())
                .expirationTime(Date
                        .from(Instant
                                .now()
                                .plus(expMin, ChronoUnit.MINUTES)
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

    public String generateRefreshToken(User user){
        return generateToken(user, REFRESHABLE_DURATION); // 7 days
    }
}
