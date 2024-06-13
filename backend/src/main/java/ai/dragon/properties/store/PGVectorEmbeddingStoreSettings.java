package ai.dragon.properties.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PGVectorEmbeddingStoreSettings {
    private String host;
    private Integer port;
    private String database;
    private String user; // TODO Ability to use env var
    private String password; // TODO Ability to use env var

    public PGVectorEmbeddingStoreSettings() {
        port = 5432;
    }
}
