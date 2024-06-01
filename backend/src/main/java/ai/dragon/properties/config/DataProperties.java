package ai.dragon.properties.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataProperties {
    public static final String PREFIX = "dragon.data";

    private String path;
    private String db;
}
