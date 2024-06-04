package ai.dragon.dto.openai.completion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiCompletionUsage {
    private Integer completion_tokens;
    private Integer prompt_tokens;
    private Integer total_tokens;
}
