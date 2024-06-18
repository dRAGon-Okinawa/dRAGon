package ai.dragon.properties.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LanguageModelSettings {
    private String apiKey;
    private String modelName;
    private String userIdentifier;
    private Double temperature;
    private Integer maxTokens;
}
