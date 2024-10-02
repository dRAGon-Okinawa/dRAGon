package ai.dragon.util.langchain4j.web.search.searxng;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SearXNGSearchResultInfobox {
    private String infobox;
    private String id;
    private String content;
    private List<SearXNGSearchResultInfoboxUrl> urls;
    private List<SearXNGSearchResultInfoboxAttribute> attributes;

    @JsonAlias("img_src")
    private String imgSrc;
}
