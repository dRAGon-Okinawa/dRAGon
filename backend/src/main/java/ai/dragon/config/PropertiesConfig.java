package ai.dragon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.dragon.properties.config.DataProperties;

@Configuration
public class PropertiesConfig {
    @Bean
    @ConfigurationProperties(prefix = DataProperties.PREFIX)
    public DataProperties dataProperties() {
        return new DataProperties();
    }
}
