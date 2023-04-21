package com.shmet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * @author
 */
@Configuration
@EnableScheduling
@EnableAsync
public class AsyncTaskExecutorConfig extends AsyncConfigurerSupport {

  private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
  private static final int MAX_SIZE = Runtime.getRuntime().availableProcessors() * 2;

  @Bean
  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(100);
    executor.setQueueCapacity(10);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.setThreadNamePrefix("Ems-Async-");
    executor.initialize();
    return executor;
  }

  @Bean
  public ThreadPoolExecutor executor() {
    return new ThreadPoolExecutor(
        CORE_SIZE,//设置核心线程数
        Math.min(MAX_SIZE, 100),
        180,
        TimeUnit.SECONDS,
        new LinkedBlockingDeque<>(400),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy()
    );
  }

}
