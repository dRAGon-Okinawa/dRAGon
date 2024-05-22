package ai.dragon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "dragon.data")
@Getter
@Setter
public class DataProperties {
    private String path;
    private String db;

    public DataProperties() {
        path = ":temp:";
        db = ":memory:";
    }
}
