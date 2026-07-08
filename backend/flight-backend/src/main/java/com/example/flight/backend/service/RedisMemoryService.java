package com.example.flight.backend.service;

import com.example.flight.backend.dto.response.RedisMemoryResponse;
import java.util.Objects;
import java.util.Properties;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisMemoryService {

    private final StringRedisTemplate redisTemplate;

    public RedisMemoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisMemoryResponse readMemory() {
        try (RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()) {
            Properties info = connection.serverCommands().info("memory");
            if (info == null) {
                return new RedisMemoryResponse(0, "0B", "UNAVAILABLE", "Redis INFO memory returned no data");
            }

            return new RedisMemoryResponse(
                    parseLong(info.getProperty("used_memory")),
                    info.getProperty("used_memory_human", "unknown"),
                    "OK",
                    null
            );
        } catch (RuntimeException exception) {
            return new RedisMemoryResponse(0, "unknown", "ERROR", exception.getMessage());
        }
    }

    private static long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
