package ai.dragon.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = DataProperties.PREFIX)
@Getter
@Setter
public class DataProperties {
    public static final String PREFIX = "dragon.data";

    private String path;
    private String db;
}
