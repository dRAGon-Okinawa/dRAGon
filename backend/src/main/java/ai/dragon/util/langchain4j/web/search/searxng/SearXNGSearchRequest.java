package ai.dragon.util.langchain4j.web.search.searxng;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Getter
@Builder
class SearXNGSearchRequest {
    private String q;
    private String engines;
    private String timeRange;
    private Integer safeSearch;

    @Default
    private String categories = "general";
    
    @Default
    private String language = "en";

    @Default
    private Integer pageno = 1;
    
    @Default
    private String format = "json";
}
