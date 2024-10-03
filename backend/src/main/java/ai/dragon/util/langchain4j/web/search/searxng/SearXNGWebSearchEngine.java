package ai.dragon.util.langchain4j.web.search.searxng;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dev.langchain4j.internal.Utils;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchInformationResult;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import dev.langchain4j.web.search.WebSearchRequest;
import dev.langchain4j.web.search.WebSearchResults;
import lombok.Builder;

public class SearXNGWebSearchEngine implements WebSearchEngine {
    public static final URI NONE_URI = URI.create("http://none");

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/";

    private final SearXNGClient searXNGClient;

    @Builder
    public SearXNGWebSearchEngine(String baseUrl,
            Duration timeout) {
        this.searXNGClient = SearXNGClient.builder()
                .baseUrl(Utils.getOrDefault(baseUrl, DEFAULT_BASE_URL))
                .timeout(Utils.getOrDefault(timeout, Duration.ofSeconds(10)))
                .build();
    }

    @Override
    public WebSearchResults search(WebSearchRequest webSearchRequest) {
        SearXNGSearchRequest request = SearXNGSearchRequest.builder()
                .q(webSearchRequest.searchTerms())
                .format("json")
                .language(webSearchRequest.language())
                .pageno(webSearchRequest.startPage())
                .maxResults(webSearchRequest.maxResults())
                .safeSearch(webSearchRequest.safeSearch() ? 1 : 0)
                .build();

        SearXNGResponse searXNGResponse = searXNGClient.search(request);

        List<WebSearchOrganicResult> results = searXNGResponse.getResults().stream()
                .map(SearXNGWebSearchEngine::toWebSearchOrganicResult)
                .collect(Collectors.toList());

        if (webSearchRequest.maxResults() != null
                && results.size() > webSearchRequest.maxResults()) {
            results = results.subList(0, webSearchRequest.maxResults());
        }

        if (additionalParamsBoolean(webSearchRequest, "appendInfoboxes", false)
                && searXNGResponse.getInfoboxes() != null) {
            for (SearXNGSearchResultInfobox infobox : searXNGResponse.getInfoboxes()) {
                WebSearchOrganicResult infoboxResult = WebSearchOrganicResult.from(
                        infobox.getInfobox(),
                        URI.create(infobox.getId()),
                        null,
                        infobox.getContent(),
                        Collections.singletonMap("score", "0.9"));
                results.add(infoboxResult);
            }
        }

        if (additionalParamsBoolean(webSearchRequest, "appendAnswers", false) && searXNGResponse.getAnswers() != null) {
            for (String answer : searXNGResponse.getAnswers()) {
                WebSearchOrganicResult answerResult = WebSearchOrganicResult.from(
                        webSearchRequest.searchTerms(),
                        NONE_URI,
                        null,
                        answer,
                        Collections.singletonMap("score", "0.7"));
                results.add(0, answerResult);
            }
        }

        if (additionalParamsBoolean(webSearchRequest, "appendSuggestions", false)
                && searXNGResponse.getSuggestions() != null) {
            for (String suggestion : searXNGResponse.getSuggestions()) {
                WebSearchOrganicResult suggestionResult = WebSearchOrganicResult.from(
                        webSearchRequest.searchTerms(),
                        NONE_URI,
                        suggestion,
                        null,
                        Collections.singletonMap("score", "0.5"));
                results.add(suggestionResult);
            }
        }

        return WebSearchResults.from(WebSearchInformationResult.from((long) results.size()), results);
    }

    public static SearXNGWebSearchEngine withBaseUrl(String baseUrl) {
        return builder().baseUrl(baseUrl).build();
    }

    private static WebSearchOrganicResult toWebSearchOrganicResult(SearXNGSearchResult searXNGSearchResult) {
        return WebSearchOrganicResult.from(searXNGSearchResult.getTitle(),
                URI.create(searXNGSearchResult.getUrl()),
                searXNGSearchResult.getContent(),
                null,
                Collections.singletonMap("score", String.valueOf(searXNGSearchResult.getScore())));
    }

    private boolean additionalParamsBoolean(WebSearchRequest webSearchRequest, String key, boolean defaultValue) {
        if (!webSearchRequest.additionalParams().containsKey(key)) {
            return defaultValue;
        }
        return webSearchRequest.additionalParams().get(key) instanceof Boolean
                && Boolean.TRUE.equals(webSearchRequest.additionalParams().get(key));
    }
}
