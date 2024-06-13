package ai.dragon.dto.openai.completion;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiCompletionResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<OpenAiCompletionChoice> choices;
    private OpenAiCompletionUsage usage;
}
