package ai.dragon.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTest.class);

    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;

    @Value("${DRAGON_CICD:}")
    private String dragonCicd;

    private boolean isRunningInCICD() {
        boolean isRunningInCICD = dragonCicd != null && "true".equalsIgnoreCase(dragonCicd);
        LOGGER.info("isRunningInCICD: {}", isRunningInCICD);
        return isRunningInCICD;
    }

    private boolean isOpenAiApiKeySet() {
        boolean isOpenAiApiKeySet = openaiApiKey != null && !openaiApiKey.isEmpty();
        LOGGER.info("isOpenAiApiKeySet: {}", isOpenAiApiKeySet);
        return isOpenAiApiKeySet;
    }

    protected boolean canRunOpenAiRelatedTests() {
        boolean canRunOpenAiRelatedTests = isRunningInCICD() || isOpenAiApiKeySet();
        LOGGER.info("canRunOpenAiRelatedTests: {}", canRunOpenAiRelatedTests);
        return canRunOpenAiRelatedTests;
    }

    protected boolean canRunSearXNGRelatedTests() {
        boolean canRunSearXNGRelatedTests = !isRunningInCICD();
        LOGGER.info("canRunSearXNGRelatedTests: {}", canRunSearXNGRelatedTests);
        return canRunSearXNGRelatedTests;
    }
}
