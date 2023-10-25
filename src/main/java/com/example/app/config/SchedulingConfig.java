package com.example.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Value("${scheduler.schedulerPoolsize}")
    private int poolSize;

    @Value("${scheduler.schedulerThreadName}")
    private String threadName;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(schedulerExecutor());
    }

    private Executor schedulerExecutor() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(poolSize);
        executor.setThreadNamePrefix(threadName);
        executor.initialize();
        return executor;
    }
}
