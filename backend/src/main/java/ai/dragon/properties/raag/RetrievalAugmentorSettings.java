package ai.dragon.properties.raag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrievalAugmentorSettings {
    public static final Integer DEFAULT_MAX_MESSAGES = 10;
    public static final Integer DEFAULT_MAX_TOKENS = 3000;

    @Builder.Default
    private Boolean rewriteQuery = false;

    @Builder.Default
    private Integer historyMaxMessages = DEFAULT_MAX_MESSAGES;

    @Builder.Default
    private Integer historyMaxTokens = DEFAULT_MAX_TOKENS;
}
