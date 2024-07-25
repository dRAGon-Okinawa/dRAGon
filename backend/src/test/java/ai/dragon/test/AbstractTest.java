package ai.dragon.test;

public abstract class AbstractTest {
    protected boolean canRunOpenAiRelatedTests() {
        return System.getenv("DRAGON_CICD") != null || System.getenv("OPENAI_API_KEY") != null;
    }
}
