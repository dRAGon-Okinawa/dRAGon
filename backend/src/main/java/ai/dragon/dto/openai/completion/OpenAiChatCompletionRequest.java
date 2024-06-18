package ai.dragon.dto.openai.completion;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class OpenAiChatCompletionRequest extends OpenAiRequest {
    @NotEmpty
    @NotNull
    private List<OpenAiCompletionMessage> messages;
    
    public OpenAiChatCompletionRequest() {
        super();
    }
}
