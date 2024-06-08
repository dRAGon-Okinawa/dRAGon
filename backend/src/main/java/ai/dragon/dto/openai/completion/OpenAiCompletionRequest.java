package ai.dragon.dto.openai.completion;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OpenAiCompletionRequest {
    @NotBlank
    @NotNull
    private String model;

    @NotNull
    private Object prompt;

    private Integer max_tokens;
    private Boolean stream;
    private Double temperature;
    private String user;
}
