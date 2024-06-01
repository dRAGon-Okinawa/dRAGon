package ai.dragon.properties.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingModelSettings {
    private String apiKey;
}
