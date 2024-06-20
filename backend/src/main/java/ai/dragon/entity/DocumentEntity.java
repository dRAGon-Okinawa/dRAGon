package ai.dragon.entity;

import java.util.List;
import java.util.UUID;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "document")
@Schema(name = "Document", description = "Document Entity")
@Getter
@Setter
public class DocumentEntity implements AbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Document")
    private UUID uuid;

    @NotNull
    @Schema(description = "Identifier of the Silo")
    private UUID siloIdentifier;

    @NotNull
    @Schema(description = "Name of the Document")
    private String name;

    @NotNull
    @Schema(description = "Flag to allow indexing of the Document")
    private Boolean allowIndexing;

    @Schema(description = "Metdata to be linked to the Document in the form of `key = value` pairs.")
    private List<String> metadata;
}
