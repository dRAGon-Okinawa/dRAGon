package ai.dragon.properties.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingSettings {
    private String apiKey;
    private Integer chunkSize = 500;
    private Integer chunkOverlap = 50;
}
