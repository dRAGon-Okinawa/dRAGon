package ai.dragon.properties.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultIngestorLoaderSettings {
    public static final String SCHEDULE_MANUAL = "manual";
    public static final String DEFAULT_TIMEZONE = "UTC";

    // Cron expression or "manual"
    private String schedule; 

    // Timezone of the schedule
    private String timezone;

    // Index new discovered documents during ingestion
    private Boolean indexNewDiscoveredDocuments;

    public DefaultIngestorLoaderSettings() {
        schedule = SCHEDULE_MANUAL;
        timezone = DEFAULT_TIMEZONE;
        indexNewDiscoveredDocuments = true;
    }
}
