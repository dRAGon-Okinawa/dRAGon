package ai.dragon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.dto.embedding.store.EmbeddingStoreSearchRequest;
import ai.dragon.entity.SiloEntity;
import ai.dragon.enumeration.EmbeddingModelType;
import ai.dragon.enumeration.IngestorLoaderType;
import ai.dragon.enumeration.VectorStoreType;
import ai.dragon.repository.SiloRepository;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;

@SpringBootTest
@ActiveProfiles("test")
public class EmbeddingStoreServiceTest {
    private static final String SILONAME = "Searchy_Silo";

    @Autowired
    private EmbeddingStoreService embeddingStoreService;

    @Autowired
    private SiloRepository siloRepository;

    @BeforeAll
    static void prepare(@Autowired SiloRepository siloRepository, @Autowired IngestorService ingestorService)
            throws Exception {
        siloRepository.deleteAll();

        String ragResourcesPath = "src/test/resources/rag_documents/sunspots";
        File ragResources = new File(ragResourcesPath);
        String ragResourcesAbsolutePath = ragResources.getAbsolutePath();
        assertNotNull(ragResourcesAbsolutePath);

        SiloEntity silo = new SiloEntity();
        silo.setUuid(UUID.randomUUID());
        silo.setName(SILONAME);
        silo.setEmbeddingModel(EmbeddingModelType.BgeSmallEnV15QuantizedEmbeddingModel);
        silo.setEmbeddingSettings(List.of(
                "chunkSize=1000",
                "chunkOverlap=100"));
        silo.setVectorStore(VectorStoreType.InMemoryEmbeddingStore);
        silo.setIngestorLoader(IngestorLoaderType.FileSystem);
        silo.setIngestorSettings(List.of(
                String.format("paths[]=%s", ragResourcesAbsolutePath),
                "recursive=false",
                "pathMatcher=regex:.*SunSpots\\.pdf"));
        siloRepository.save(silo);

        ingestorService.runSiloIngestion(silo, ingestProgress -> {
            System.out.println("Ingest progress: " + ingestProgress);
        }, ingestLogMessage -> {
            System.out.println(ingestLogMessage.getMessage());
        });
    }

    @AfterAll
    static void clean(@Autowired SiloRepository siloRepository) {
        siloRepository.deleteAll();
    }

    @Test
    void searchSilo() throws Exception {
        SiloEntity silo = siloRepository.findUniqueByFieldValue("name", SILONAME)
                .orElseThrow(() -> new IllegalArgumentException("Silo not found"));
        double minScore = EmbeddingStoreSearchRequest.DEFAULT_MIN_SCORE;
        int maxResults = EmbeddingStoreSearchRequest.DEFAULT_MAX_RESULTS;

        List<EmbeddingMatch<TextSegment>> searchResult = embeddingStoreService.search(silo,
                EmbeddingStoreSearchRequest.builder()
                        .query("Why sunspots are dark?")
                        .maxResults(maxResults)
                        .minScore(minScore)
                        .build());
        assertNotNull(searchResult);
        assertNotEquals(0, searchResult.size());

        double firstScore = searchResult.get(0).score();
        double lastScore = searchResult.get(searchResult.size() - 1).score();
        assertEquals(true, lastScore >= minScore);
        assertEquals(true, firstScore >= lastScore);
    }

    @Test
    void searchSiloWithMetadataFilter() throws Exception {
        SiloEntity silo = siloRepository.findUniqueByFieldValue("name", SILONAME)
                .orElseThrow(() -> new IllegalArgumentException("Silo not found"));
        double minScore = EmbeddingStoreSearchRequest.DEFAULT_MIN_SCORE;
        int maxResults = EmbeddingStoreSearchRequest.DEFAULT_MAX_RESULTS;

        List<EmbeddingMatch<TextSegment>> searchResult = embeddingStoreService.search(silo,
                EmbeddingStoreSearchRequest.builder()
                        .query("Why sunspots are dark?")
                        .maxResults(maxResults)
                        .minScore(minScore)
                        .filter(MetadataFilterBuilder.metadataKey("document_name")
                                .isEqualTo("SunSpots.pdf"))
                        .build());
        assertNotNull(searchResult);
        assertNotEquals(0, searchResult.size());
    }
}
