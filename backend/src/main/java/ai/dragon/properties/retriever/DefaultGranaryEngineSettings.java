package ai.dragon.properties.retriever;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ai.dragon.enumeration.GranaryEngine;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultGranaryEngineSettings {
    private GranaryEngine engine;
}
