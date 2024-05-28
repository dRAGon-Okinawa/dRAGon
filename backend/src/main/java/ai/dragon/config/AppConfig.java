package ai.dragon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import ai.dragon.properties.DataProperties;

@Configuration
@EnableConfigurationProperties(DataProperties.class)
public class AppConfig {
}
