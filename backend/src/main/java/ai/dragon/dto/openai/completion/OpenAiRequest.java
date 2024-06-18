package ai.dragon.dto.openai.completion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OpenAiRequest {
    @NotBlank
    @NotNull
    @Schema(description = "Name of the Farm 'Raag Model' to be used.")
    private String model;

    private Integer max_tokens;
    private Boolean stream;
    private Double temperature;
    private String user;
}
