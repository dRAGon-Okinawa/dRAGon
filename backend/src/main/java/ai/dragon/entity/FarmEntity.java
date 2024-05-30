package ai.dragon.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;

import ai.dragon.enumeration.EmbeddingModelType;
import ai.dragon.enumeration.VectorStoreType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "farm")
@Schema(name = "Farm", description = "Farm Entity")
@Indices({ @Index(fields = "name", type = IndexType.UNIQUE) })
@Getter
@Setter
public class FarmEntity implements AbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Farm")
    private UUID uuid;

    @NotNull
    @Schema(description = "Name of the Farm. Must be unique.")
    private String name;

    @NotNull
    @Schema(description = "Java Class to be used for the Vector Store", example = "InMemoryEmbeddingStore")
    private VectorStoreType vectorStoreType;

    @NotNull
    @Schema(description = "Type to be used for the Embedding Model", example = "BgeSmallEnV15QuantizedEmbeddingModel")
    private EmbeddingModelType embeddingModelType;

    @Schema(description = """
            List of Silo UUIDs to be linked to the Farm.
            A farm is a collection of Silos, each Silo is a collection of Documents.""")
    private List<UUID> silos;

    public FarmEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Farm %s", this.uuid.toString());
        this.vectorStoreType = VectorStoreType.InMemoryEmbeddingStore;
        this.embeddingModelType = EmbeddingModelType.BgeSmallEnV15QuantizedEmbeddingModel;
        this.silos = new ArrayList<UUID>();
    }
}
