package ai.dragon.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configurable
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor threadPoolTaskExecutor() {
    	ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    	threadPoolTaskExecutor.setMaxPoolSize(10);
    	
    	return threadPoolTaskExecutor;
    }
}
