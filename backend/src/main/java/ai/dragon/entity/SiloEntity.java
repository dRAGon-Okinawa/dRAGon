package ai.dragon.entity;

import java.util.UUID;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "silo")
@Schema(name = "Silo", description = "Silo Entity")
@Getter
@Setter
public class SiloEntity implements IAbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Silo")
    private UUID uuid;

    @NotNull
    @Schema(description = "Name of the Silo")
    private String name;

    @NotNull
    @Schema(description = "Java Class to be used for the Vector Store", example = "InMemoryEmbeddingStore")
    private String vectorStoreClass;

    @NotNull
    @Schema(description = "Java Class to be used for the Embedding Model", example = "BgeSmallEnV15QuantizedEmbeddingModel")
    private String embeddingModelClass;

    public SiloEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Silo %s", this.uuid.toString());
        this.vectorStoreClass = InMemoryEmbeddingStore.class.getCanonicalName();
        this.embeddingModelClass = BgeSmallEnV15QuantizedEmbeddingModel.class.getCanonicalName();
    }
}
