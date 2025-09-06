package com.example.utils;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GlobalRequestLimiter {

    // 限流器，每秒允许100个请求（QPS）
    private final RateLimiter limiter = RateLimiter.create(100.0);

    /**
     * 尝试获取令牌，最多等待 timeoutMillis 毫秒
     * @param timeoutMillis 最大等待时间（毫秒）
     * @return 是否成功获取
     */
    public boolean tryAcquireWithTimeout(long timeoutMillis) {
        return limiter.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}

