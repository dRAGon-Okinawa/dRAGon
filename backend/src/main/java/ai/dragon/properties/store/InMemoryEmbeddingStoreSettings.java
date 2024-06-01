package ai.dragon.properties.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InMemoryEmbeddingStoreSettings {
    private String persistance;

    public InMemoryEmbeddingStoreSettings() {
        persistance = ":memory:";
    }
}
