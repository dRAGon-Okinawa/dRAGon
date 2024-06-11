package ai.dragon.dto.openai.completion;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OpenAiCompletionRequest {
    @NotBlank
    @NotNull
    @Schema(description = "Name of the Farm 'Raag Model' to be used.")
    private String model;

    @NotNull
    private Object prompt;

    private Integer max_tokens;
    private Boolean stream;
    private Double temperature;
    private String user;
}
