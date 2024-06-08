package ai.dragon.dto.openai.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiChatCompletionChoice {
    private String finish_reason;
    private Integer index;

    @JsonInclude(Include.NON_NULL)
    private OpenAiCompletionMessage message;

    @JsonInclude(Include.NON_NULL)
    private OpenAiCompletionMessage delta;
}
