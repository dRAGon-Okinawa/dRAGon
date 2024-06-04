package ai.dragon.util.embedding.search;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmbeddingMatchResponse {
    private Double score;
    private String text;
    private Map<String, String> metadata;
}
