package ai.dragon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ai.dragon.entity.GranaryEntity;
import ai.dragon.properties.retriever.DefaultGranaryEngineSettings;
import ai.dragon.properties.retriever.web.SearXNGEngineSettings;
import ai.dragon.util.KVSettingUtil;
import ai.dragon.util.langchain4j.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.web.search.WebSearchEngine;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class GranaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ContentRetriever buildContentRetriever(GranaryEntity granary, HttpServletRequest servletRequest) {
        switch (granary.getEngineType()) {
            case WebSearchEngine:
                return buildWebSearchContentRetriever(granary);
            default:
                logger.warn(
                        "Granary '{}' (UUID '{}') has an unknown Engine Type '{}', no content retriever will be made",
                        granary.getName(), granary.getUuid(), granary.getEngineType());
                return null;
        }
    }

    private ContentRetriever buildWebSearchContentRetriever(GranaryEntity granary) {
        DefaultGranaryEngineSettings engineSettings = KVSettingUtil.kvSettingsToObject(granary.getEngineSettings(),
                DefaultGranaryEngineSettings.class);
        switch (engineSettings.getEngine()) {
            case SearXNG:
                return buildSearXNGWebSearchContentRetriever(granary);
            default:
                logger.warn(
                        "Granary '{}' (UUID '{}') has an unknown Engine '{}', no content retriever will be made",
                        granary.getName(), granary.getUuid(), engineSettings.getEngine());
                return null;
        }
    }

    private ContentRetriever buildSearXNGWebSearchContentRetriever(GranaryEntity granary) {
        SearXNGEngineSettings engineSettings = KVSettingUtil.kvSettingsToObject(granary.getEngineSettings(),
                SearXNGEngineSettings.class);
        WebSearchEngine webSearchEngine = SearXNGWebSearchEngine
                .builder()
                .baseUrl(engineSettings.getBaseUrl())
                .appendAnswers(engineSettings.getAppendAnswers())
                .appendInfoboxes(engineSettings.getAppendInfoboxes())
                .appendSuggestions(engineSettings.getAppendSuggestions())
                .build();
        WebSearchContentRetriever contentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .maxResults(engineSettings.getMaxResults())
                .build();

        return contentRetriever;
    }
}
