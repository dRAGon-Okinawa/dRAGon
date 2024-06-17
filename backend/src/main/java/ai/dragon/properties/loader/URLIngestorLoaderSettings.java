package ai.dragon.properties.loader;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class URLIngestorLoaderSettings extends DefaultIngestorLoaderSettings {
    private List<String> urls;

    public URLIngestorLoaderSettings() {
        super();
    }
}
