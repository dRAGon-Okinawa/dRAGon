package ai.dragon.entity;

import java.util.Date;
import java.util.UUID;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity(value = "document")
@Schema(name = "Document", description = "Document Entity")
@Indices(@Index(fields = { "siloIdentifier", "location" }, type = IndexType.UNIQUE))
@Data
@Builder
@AllArgsConstructor
public class DocumentEntity implements AbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Document")
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @NotNull
    @Schema(description = "Identifier of the Silo")
    private UUID siloIdentifier;

    @NotNull
    @Schema(description = "Name of the Document")
    private String name;

    @NotNull
    @Schema(description = "Location of the Document")
    private String location;

    @NotNull
    @Schema(description = "Flag to allow indexing of the Document")
    @Builder.Default
    private Boolean allowIndexing = true;

    @Schema(description = "Date when the Document was last seen")
    private Date lastSeen;

    @Schema(description = "Date when the Document was last indexed")
    private Date lastIndexed;

    public DocumentEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Document %s", this.uuid.toString());
        this.allowIndexing = true;
    }
}
