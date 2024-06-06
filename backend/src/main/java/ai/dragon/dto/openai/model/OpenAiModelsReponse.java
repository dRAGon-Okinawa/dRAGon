package ai.dragon.dto.openai.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenAiModelsReponse {
    @Builder.Default
    private String object = "list";
    
    private List<OpenAiModel> data;
}
