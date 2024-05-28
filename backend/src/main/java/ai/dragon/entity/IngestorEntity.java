package ai.dragon.entity;

import java.util.UUID;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "ingestor")
@Schema(name = "Ingestor", description = "Ingestor Entity")
@Indices({
        @Index(fields = "name", type = IndexType.UNIQUE)
})
@Getter
@Setter
public class IngestorEntity implements IAbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Ingestor")
    private UUID uuid;

    @NotNull
    @Schema(description = "Name of the Ingestor. Must be unique.")
    private String name;

    public IngestorEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Ingestor %s", this.uuid.toString());
    }
}
