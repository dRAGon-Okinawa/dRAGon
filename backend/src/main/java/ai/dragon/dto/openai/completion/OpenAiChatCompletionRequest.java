package ai.dragon.dto.openai.completion;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OpenAiChatCompletionRequest {
    @NotBlank
    @NotNull
    private String model;

    @NotEmpty
    @NotNull
    private List<OpenAiCompletionMessage> messages;
    
    private Integer max_tokens;
    private Boolean stream;
    private Double temperature;
    private String user;
}
