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

import ai.dragon.entity.SiloEntity;
import ai.dragon.enumeration.EmbeddingModelType;
import ai.dragon.enumeration.IngestorLoaderType;
import ai.dragon.enumeration.VectorStoreType;
import ai.dragon.repository.SiloRepository;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;

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
    void querySilo() throws Exception {
        SiloEntity silo = siloRepository.findUniqueByFieldValue("name", SILONAME)
                .orElseThrow(() -> new IllegalArgumentException("Silo not found"));
        double minScore = 0.8;
        List<EmbeddingMatch<TextSegment>> searchResult = embeddingStoreService.query(silo, "Why sunspots are dark?",
                10, minScore);
        assertNotNull(searchResult);
        assertNotEquals(0, searchResult.size());

        double firstScore = searchResult.get(0).score();
        double lastScore = searchResult.get(searchResult.size() - 1).score();
        assertEquals(true, lastScore >= minScore);
        assertEquals(true, firstScore >= lastScore);
    }
}
