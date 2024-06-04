package ai.dragon.dto.openai.completion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiCompletionMessage {
    @NotNull
    @NotBlank
    private String role;

    @NotNull
    @NotBlank
    private String content;

    private String name;
}
