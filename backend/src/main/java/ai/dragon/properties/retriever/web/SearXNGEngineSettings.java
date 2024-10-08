package ai.dragon.properties.retriever.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ai.dragon.properties.retriever.DefaultGranaryEngineSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearXNGEngineSettings extends DefaultGranaryEngineSettings {
    private String baseUrl;
    private Integer maxResults;
    private Boolean appendInfoboxes;
    private Boolean appendSuggestions;
    private Boolean appendAnswers;

    public SearXNGEngineSettings() {
        super();
        baseUrl = "http://localhost:8080";
        maxResults = 10;
        appendInfoboxes = false;
        appendSuggestions = false;
        appendAnswers = false;
    }
}
