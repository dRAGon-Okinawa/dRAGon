package ai.dragon.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;

import ai.dragon.enumeration.LanguageModelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "farm")
@Schema(name = "Farm", description = "Farm Entity")
@Indices({
        @Index(fields = "name", type = IndexType.UNIQUE),
        @Index(fields = "raagIdentifier", type = IndexType.UNIQUE)
})
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

    @Schema(description = "Identifier for the 'Raag Model' to be used for the RaaG API. Must be unique")
    @Pattern(regexp = "^[a-z0-9\\-]+$", message = "Must be alphanumeric, hyphens allowed")
    private String raagIdentifier;

    @Schema(description = """
            List of Silo UUIDs to be linked to the Farm.
            A Farm is a collection of Silos, which each Silo is a collection of Documents.""")
    private List<UUID> silos;

    @Schema(description = "Language Model to be used for the RaaG API")
    private LanguageModelType languageModel;

    @Schema(description = "Settings to be linked to the Farm's Language Model in the form of `key = value` pairs.")
    private List<String> languageModelSettings;

    @Schema(description = "Enable/disable Query Rewriting. Improve RAG Performance. Uses Chat History. Need LLM calls => Costs money.", defaultValue = "false")
    private Boolean rewriteQuery;

    public FarmEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Farm %s", this.uuid.toString());
        this.silos = new ArrayList<UUID>();
        this.raagIdentifier = UUID.randomUUID().toString();
        this.languageModel = LanguageModelType.OpenAiModel;
        this.rewriteQuery = false;
    }
}
