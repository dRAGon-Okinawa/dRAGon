package ai.dragon.properties.raag;

import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter.FallbackStrategy;
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
    public static final PromptTemplate DEFAULT_LANGUAGE_QUERY_ROUTER_PROMPT_TEMPLATE = LanguageModelQueryRouter.DEFAULT_PROMPT_TEMPLATE;
    public static final FallbackStrategy DEFAULT_LANGUAGE_QUERY_ROUTER_FALLBACK_STRATEGY = FallbackStrategy.FAIL;

    @Builder.Default
    private Boolean rewriteQuery = false;

    @Builder.Default
    private Integer historyMaxMessages = DEFAULT_MAX_MESSAGES;

    @Builder.Default
    private Integer historyMaxTokens = DEFAULT_MAX_TOKENS;

    @Builder.Default
    private PromptTemplate languageQueryRouterPromptTemplate = DEFAULT_LANGUAGE_QUERY_ROUTER_PROMPT_TEMPLATE;

    @Builder.Default
    private FallbackStrategy languageQueryRouterFallbackStrategy = DEFAULT_LANGUAGE_QUERY_ROUTER_FALLBACK_STRATEGY;
}
