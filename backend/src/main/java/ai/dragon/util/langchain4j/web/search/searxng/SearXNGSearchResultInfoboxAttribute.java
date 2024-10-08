package ai.dragon.util.langchain4j.web.search.searxng;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearXNGSearchResultInfoboxAttribute {
    private String label;
    private String value;
    private String entity;
}
