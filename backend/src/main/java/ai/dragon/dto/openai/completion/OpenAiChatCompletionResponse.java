package ai.dragon.dto.openai.completion;

import java.util.List;

import lombok.Data;

@Data
public class OpenAiChatCompletionResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<OpenAiChatCompletionChoice> choices;
    private OpenAiCompletionUsage usage;
}
