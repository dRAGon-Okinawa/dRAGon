package ai.dragon.dto.openai.completion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiCompletionChoice {
    private String finish_reason;
    private Integer index;
    private String text;
}
