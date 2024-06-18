package ai.dragon.dto.openai.completion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class OpenAiCompletionRequest extends OpenAiRequest {
    @NotNull
    private Object prompt;
}
