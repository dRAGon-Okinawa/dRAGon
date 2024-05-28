package ai.dragon.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataProperties {
    public static final String PREFIX = "dragon.data";

    private String path;
    private String db;
}
