package ai.dragon.entity;

import java.util.List;
import java.util.UUID;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;

import ai.dragon.enumeration.EmbeddingModelType;
import ai.dragon.enumeration.IngestorType;
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
    public static final String DEFAULT_CRON_EXPRESSION = "*/15 * * * *";

    @Id
    @NotNull
    @Schema(description = "Identifier of the Silo")
    private UUID uuid;

    @NotNull
    @Schema(description = "Name of the Silo. Must be unique.")
    private String name;

    @NotNull
    @Schema(description = "Java Class to be used for the Vector Store", example = "InMemoryEmbeddingStore")
    private VectorStoreType vectorStoreType;

    @NotNull
    @Schema(description = "Type to be used for the Embedding Model", example = "BgeSmallEnV15QuantizedEmbeddingModel")
    private EmbeddingModelType embeddingModelType;

    @NotNull
    @Schema(description = "Type of the Silo's Ingestor Type")
    private IngestorType ingestorType;

    @NotNull
    @Schema(description = "Cron Expression for the Silo's Ingestor Job", example = "Launch the Silo ingestor every 15 minutes : */15 * * * *")
    private String ingestorSchedule;

    @Schema(description = "Settings to be linked to the Silo's Ingestor in the form of `key = value` pairs.")
    private List<String> ingestorSettings;

    public SiloEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Silo %s", this.uuid.toString());
        this.vectorStoreType = VectorStoreType.InMemoryEmbeddingStore;
        this.embeddingModelType = EmbeddingModelType.BgeSmallEnV15QuantizedEmbeddingModel;
        this.ingestorType = IngestorType.FileSystem;
        this.ingestorSchedule = DEFAULT_CRON_EXPRESSION;
    }
}
