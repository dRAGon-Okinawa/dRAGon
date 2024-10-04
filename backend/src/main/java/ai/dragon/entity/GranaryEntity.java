package ai.dragon.entity;

import java.util.List;
import java.util.UUID;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;

import ai.dragon.enumeration.GranaryEngineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "granary")
@Schema(name = "Granary", description = "Granary Entity")
@Indices({ @Index(fields = "name", type = IndexType.UNIQUE) })
@Getter
@Setter
public class GranaryEntity implements AbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Granary")
    private UUID uuid;

    @NotNull
    @Schema(description = "Name of the Granary. Must be unique.")
    private String name;

    @Schema(description = "Description of the Granary. Used by the Language Query Router to choose the best Silo / Granary among Farm chain.")
    private String description;

    @NotNull
    @Schema(description = "Engine type to be used for the Vector Store", example = "WebSearchEngine")
    private GranaryEngineType engineType;

    @Schema(description = "Settings to be linked to the Granary's Engine in the form of `key = value` pairs.")
    private List<String> engineSettings;

    public GranaryEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Granary %s", this.uuid.toString());
        this.engineType = GranaryEngineType.WebSearchEngine;
    }
}
