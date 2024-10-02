package ai.dragon.util.langchain4j.web.search.searxng;

import java.util.List;

import org.apache.poi.hpsf.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SearXNGSearchResult {
    private String url;
    private String title;
    private String content;
    private Date publishedDate;
    private String thumbnail;
    private String engine;
    private List<String> engines;
    private List<Integer> positions;
    private Integer score;
    private String category;

    @JsonAlias("parsed_url")
    private List<String> parsedUrl;
}
