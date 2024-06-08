package ai.dragon.dto.openai.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiModel {
    private String id;
    private Long created;
    private String owned_by;

    @Builder.Default
    private String object = "model";
}
