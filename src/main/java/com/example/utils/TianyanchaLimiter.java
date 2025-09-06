package com.example.utils;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TianyanchaLimiter {

    // 每秒最多10个请求
    private final RateLimiter limiter = RateLimiter.create(10.0);

    /**
     * 尝试在最大等待时间内获取许可
     * @param timeoutMillis 最大等待时间（毫秒）
     * @return 是否成功获取许可
     */
    public boolean tryAcquireWithTimeout(long timeoutMillis) {
        return limiter.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}
