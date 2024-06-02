package ai.dragon.properties.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersistInMemoryEmbeddingStoreSettings {
    private Integer flushSecs;

    public PersistInMemoryEmbeddingStoreSettings() {
        flushSecs = 60;
    }
}
