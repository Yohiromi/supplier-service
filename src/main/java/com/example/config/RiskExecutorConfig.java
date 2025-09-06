package com.example.config;// package com.example.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class RiskExecutorConfig {

    @Bean(name = "riskExecutor")
    public ExecutorService riskExecutor() {
        int cpu = Runtime.getRuntime().availableProcessors();
        int core = Math.max(2, cpu + 1);
        int max  = Math.max(core + 1, 2 * cpu);
        int queueCapacity = 1000;

        ThreadFactory tf = r -> {
            Thread t = new Thread(r);
            t.setName("risk-exec-" + t.getId());
            t.setDaemon(true);
            return t;
        };

        return new ThreadPoolExecutor(
                core,
                max,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                tf,
                // 拒绝策略：调用方线程执行，避免直接丢请求（同时是反馈“压力大”的信号）
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
