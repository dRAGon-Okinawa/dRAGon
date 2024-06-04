package ai.dragon.properties.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ai.dragon.util.embedding.store.inmemory.persist.PersistInMemoryEmbeddingStore;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersistInMemoryEmbeddingStoreSettings {
    private Integer flushSecs;

    public PersistInMemoryEmbeddingStoreSettings() {
        flushSecs = PersistInMemoryEmbeddingStore.DEFAULT_FLUSH_SECS;
    }
}
