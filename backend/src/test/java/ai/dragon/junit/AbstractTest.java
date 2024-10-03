package ai.dragon.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTest.class);

    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;

    @Value("${DRAGON_CICD:}")
    private Boolean dragonCicd;

    protected boolean canRunOpenAiRelatedTests() {
        boolean canRunOpenAiRelatedTests = Boolean.TRUE.equals(dragonCicd) || openaiApiKey != null && !openaiApiKey.isEmpty();
        LOGGER.info("canRunOpenAiRelatedTests: {}", canRunOpenAiRelatedTests);
        return canRunOpenAiRelatedTests;
    }

    protected boolean canRunSearXNGRelatedTests() {
        boolean canRunSearXNGRelatedTests = dragonCicd == null || Boolean.FALSE.equals(dragonCicd);
        LOGGER.info("canRunSearXNGRelatedTests: {}", canRunSearXNGRelatedTests);
        return canRunSearXNGRelatedTests;
    }
}
