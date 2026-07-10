package com.zhouyi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    /**
     * Dedicated thread pool for Elasticsearch synchronization tasks.
     * Provides "peak clipping" by using a bounded queue and controlled pool sizes.
     */
    @Bean(name = "esSyncExecutor")
    public Executor esSyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Core pool size: threads that are always alive
        executor.setCorePoolSize(5);
        // Max pool size: maximum number of threads
        executor.setMaxPoolSize(10);
        // Queue capacity: tasks that can wait before being rejected or spawning new threads
        executor.setQueueCapacity(100);
        // Thread name prefix for easier debugging in logs
        executor.setThreadNamePrefix("EsSync-");
        
        // Rejection policy: CallerRunsPolicy ensures that if the queue is full and all threads are busy,
        // the task will be executed in the caller's thread (main thread), ensuring no task is lost.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        return executor;
    }
}
