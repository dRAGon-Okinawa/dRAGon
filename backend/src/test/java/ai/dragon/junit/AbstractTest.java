package ai.dragon.junit;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractTest {
    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;

    @Value("${DRAGON_CICD:}")
    private Boolean dragonCicd;

    protected boolean canRunOpenAiRelatedTests() {
        return Boolean.TRUE.equals(dragonCicd) || openaiApiKey != null && !openaiApiKey.isEmpty();
    }
}
