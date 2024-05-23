package ai.dragon.entity;

import java.util.UUID;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

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

    public SiloEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Silo %s", this.uuid.toString());
    }
}
