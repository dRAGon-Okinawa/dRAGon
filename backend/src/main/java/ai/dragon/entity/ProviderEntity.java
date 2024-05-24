package ai.dragon.entity;

import java.util.Map;
import java.util.UUID;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import ai.dragon.enumeration.ProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity(value = "provider")
@Schema(name = "Provider", description = "Provider Entity")
@Getter
@Setter
public class ProviderEntity implements IAbstractEntity {
    @Id
    @NotNull
    @Schema(description = "Identifier of the Provider")
    private UUID uuid;

    @NotNull
    @NotBlank
    @Schema(description = "Name of the Provider")
    private String name;

    @NotNull
    @Schema(description = "Type of the Provider")
    private ProviderType type;

    @Schema(description = """
            Headers to be sent to the Provider (if applicable) in the form of key-value pairs.
            Could be used for authentication with API keys, tokens, etc.""")
    private Map<String, String> httpHeaders;

    public ProviderEntity() {
        this.uuid = UUID.randomUUID();
        this.name = String.format("Provider %s", this.uuid.toString());
    }
}
