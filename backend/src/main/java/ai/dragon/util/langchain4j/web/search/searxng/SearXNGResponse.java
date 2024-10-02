package ai.dragon.util.langchain4j.web.search.searxng;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
class SearXNGResponse {
    private List<Object> corrections;
    private String query;
    private List<String> answers;
    private List<String> suggestions;
    private List<SearXNGSearchResultInfobox> infoboxes;
    private List<SearXNGSearchResult> results;
}
