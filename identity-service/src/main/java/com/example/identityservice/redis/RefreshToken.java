package com.example.identityservice.redis;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash(value = "refresh_tokens", timeToLive = 604800) // TTL = 7 days (second)
public class RefreshToken {
    @Id
    String id;

    @Indexed
    UUID userId;
}
