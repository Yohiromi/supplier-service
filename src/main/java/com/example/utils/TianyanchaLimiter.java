package com.example.utils;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

@Component
public class TianyanchaLimiter {
    private final RateLimiter limiter = RateLimiter.create(10.0); // 每秒 10 个许可

    public boolean tryAcquire() {
        return limiter.tryAcquire();
    }
}
