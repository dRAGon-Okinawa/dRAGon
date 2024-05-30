package ai.dragon.entity;

import java.util.Map;
import java.util.UUID;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;

import ai.dragon.enumeration.EmbeddingModelType;
import ai.dragon.enumeration.SiloType;
import ai.dragon.enumeration.VectorStoreType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "silo")
@Schema(name = "Silo", description = "Silo Entity")
@Indices({ @Index(fields = "name", type = IndexType.UNIQUE) })
@Getter
@Setter
public class SiloEntity implements AbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Silo")
    private UUID uuid;

    @NotNull
    @Schema(description = "Name of the Silo. Must be unique.")
    private String name;

    @Schema(description = "Type of the Silo")
    private SiloType type;

    @NotNull
    @Schema(description = "Java Class to be used for the Vector Store", example = "InMemoryEmbeddingStore")
    private VectorStoreType vectorStoreType;

    @NotNull
    @Schema(description = "Type to be used for the Embedding Model", example = "BgeSmallEnV15QuantizedEmbeddingModel")
    private EmbeddingModelType embeddingModelType;

    @Schema(description = "Settings to be linked to the Silo (if applicable) in the form of key-value pairs.")
    private Map<String, String> settings;

    public SiloEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Silo %s", this.uuid.toString());
        this.vectorStoreType = VectorStoreType.InMemoryEmbeddingStore;
        this.embeddingModelType = EmbeddingModelType.BgeSmallEnV15QuantizedEmbeddingModel;
    }
}
