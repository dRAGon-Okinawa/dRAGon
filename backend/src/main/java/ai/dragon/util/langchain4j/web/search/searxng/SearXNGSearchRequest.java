package ai.dragon.util.langchain4j.web.search.searxng;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Getter
@Builder
class SearXNGSearchRequest {
    private String q;

    @Default
    private String format = "json";
}
