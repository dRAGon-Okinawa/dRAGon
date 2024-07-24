package ai.dragon.dto.embedding.store;

import dev.langchain4j.store.embedding.filter.Filter;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmbeddingStoreSearchRequest {
    public static final int DEFAULT_MAX_RESULTS = 10;
    public static final double DEFAULT_MIN_SCORE = 0.8;

    private String query;

    @Builder.Default
    private Integer maxResults = DEFAULT_MAX_RESULTS;

    @Builder.Default
    private double minScore = DEFAULT_MIN_SCORE;

    private Filter filter;
}
