package ai.dragon.controller.api.rag;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;
import ai.dragon.service.EmbeddingStoreService;
import ai.dragon.util.embedding.search.EmbeddingMatchResponse;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/rag/search")
@Tag(name = "Search", description = "Search API Endpoints")
public class SearchRagApiController {
    @Autowired
    private EmbeddingStoreService embeddingStoreService;

    @Autowired
    private SiloRepository siloRepository;

    // TODO Silo OR Farm
    @PostMapping("/documents/silo/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Documents have been successfully retrieved.")
    @Operation(summary = "Search documents inside a Silo", description = "Search documents from the Silo.")
    public List<EmbeddingMatchResponse> searchDocumentsInSilo(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Silo") UUID uuid,
            @RequestParam(name = "maxResults", required = false, defaultValue = "10") @Parameter(description = "Max results to return") Integer maxResults,
            @RequestBody String query)
            throws Exception {
        SiloEntity silo = siloRepository.getByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
        List<EmbeddingMatchResponse> searchResults = new ArrayList<>();
        EmbeddingSearchResult<TextSegment> embeddingSearchResult = embeddingStoreService.query(silo, query, maxResults);
        for (EmbeddingMatch<TextSegment> embeddingMatch : embeddingSearchResult.matches()) {
            searchResults.add(EmbeddingMatchResponse.builder()
                    .score(embeddingMatch.score())
                    .metadata(embeddingMatch.embedded().metadata().toMap())
                    .text(embeddingMatch.embedded().text())
                    .build());
        }
        return searchResults;
    }
}
