package com.project.jvc3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    //2코어 2gb메모리 단순 crud 혹은 크지않은 작업

    //이메일 인증
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 코어 쓰레드 수
        executor.setMaxPoolSize(8); // 최대 쓰레드 수
        executor.setQueueCapacity(20); // 작업 대기 큐 크기
        executor.setThreadNamePrefix("AsyncEmailThread-"); // 쓰레드 이름 접두사
        executor.initialize();
        return executor;
    }
}
