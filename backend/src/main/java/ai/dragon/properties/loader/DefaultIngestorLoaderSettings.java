package ai.dragon.properties.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultIngestorLoaderSettings {
    public static final String DEFAULT_TIMEZONE = "UTC";

    private String schedule;
    private String timezone;

    public DefaultIngestorLoaderSettings() {
        timezone = DEFAULT_TIMEZONE;
    }
}
