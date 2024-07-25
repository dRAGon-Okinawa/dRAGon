package ai.dragon.controller.api.raag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.FarmEntity;
import ai.dragon.entity.SiloEntity;
import ai.dragon.enumeration.EmbeddingModelType;
import ai.dragon.enumeration.IngestorLoaderType;
import ai.dragon.enumeration.LanguageModelType;
import ai.dragon.enumeration.VectorStoreType;
import ai.dragon.repository.FarmRepository;
import ai.dragon.repository.SiloRepository;
import ai.dragon.service.IngestorService;
import ai.dragon.test.AbstractTest;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.langchain4j.model.mistralai.internal.api.MistralAiModelResponse;
import dev.langchain4j.model.mistralai.internal.client.MistralAiClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenAiCompatibleV1ApiControllerTest extends AbstractTest {
    @LocalServerPort
    private int serverPort;

    @BeforeAll
    static void beforeAll(@Autowired FarmRepository farmRepository,
            @Autowired SiloRepository siloRepository,
            @Autowired IngestorService ingestorService) throws Exception {
        cleanUp(farmRepository, siloRepository);

        // OpenAI settings for RaaG
        String apiKeySetting = String.format("apiKey=%s", System.getenv("OPENAI_API_KEY"));
        String modelNameSetting = "modelName=gpt-4o";

        // Farm with no silo
        FarmEntity farmWithoutSilo = new FarmEntity();
        farmWithoutSilo.setRaagIdentifier("no-silo-raag");
        farmWithoutSilo.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithoutSilo.setLanguageModelSettings(List.of(apiKeySetting, modelNameSetting));
        farmRepository.save(farmWithoutSilo);

        // Resources for the silo
        String ragResourcesPath = "src/test/resources/rag_documents/sunspots";
        File ragResources = new File(ragResourcesPath);
        String ragResourcesAbsolutePath = ragResources.getAbsolutePath();
        assertNotNull(ragResourcesAbsolutePath);

        // Silo about "Sunspots"
        SiloEntity sunspotsSilo = new SiloEntity();
        sunspotsSilo.setUuid(UUID.randomUUID());
        sunspotsSilo.setName("Sunspots Silo");
        sunspotsSilo.setEmbeddingModel(EmbeddingModelType.BgeSmallEnV15QuantizedEmbeddingModel);
        sunspotsSilo.setEmbeddingSettings(List.of(
                "chunkSize=1000",
                "chunkOverlap=100"));
        sunspotsSilo.setVectorStore(VectorStoreType.InMemoryEmbeddingStore);
        sunspotsSilo.setIngestorLoader(IngestorLoaderType.FileSystem);
        sunspotsSilo.setIngestorSettings(List.of(
                String.format("paths[]=%s", ragResourcesAbsolutePath),
                "recursive=false",
                "pathMatcher=regex:.**\\.pdf"));
        siloRepository.save(sunspotsSilo);

        // Launching ingestion of documents inside the Silo
        ingestorService.runSiloIngestion(sunspotsSilo, ingestProgress -> {
            System.out.println("Ingest progress: " + ingestProgress);
        }, ingestLogMessage -> {
            System.out.println(ingestLogMessage.getMessage());
        });

        // Farm with the Sunspots Silo
        FarmEntity farmWithSunspotsSilo = new FarmEntity();
        farmWithSunspotsSilo.setRaagIdentifier("sunspots-raag");
        farmWithSunspotsSilo.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsSilo.setLanguageModelSettings(List.of(apiKeySetting, modelNameSetting));
        farmWithSunspotsSilo.setSilos(List.of(sunspotsSilo.getUuid()));
        farmRepository.save(farmWithSunspotsSilo);
    }

    @AfterAll
    static void afterAll(@Autowired FarmRepository farmRepository, @Autowired SiloRepository siloRepository) {
        cleanUp(farmRepository, siloRepository);
    }

    static void cleanUp(FarmRepository farmRepository, SiloRepository siloRepository) {
        farmRepository.deleteAll();
        siloRepository.deleteAll();
    }

    @Test
    void listModels() throws Exception {
        MistralAiClient client = MistralAiClient.builder()
                .apiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .timeout(Duration.ofSeconds(10))
                .logRequests(false)
                .logResponses(false)
                .build();
        MistralAiModelResponse modelsResponse = client.listModels();
        assertNotNull(modelsResponse);
        assertNotEquals(0, modelsResponse.getData().size());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    void testModelDoesntExistOpenAI() {
        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .build();
        CompletionRequest request = CompletionRequest.builder()
                .model("should-not-exist")
                .prompt("Just say : 'dRAGon'")
                .build();
        OpenAiHttpException exception = assertThrows(OpenAiHttpException.class,
                () -> client.completion(request).execute());
        assertTrue(exception.code() == 404);
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    void testFarmNoSiloOpenAI() {
        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .build();
        CompletionRequest request = CompletionRequest.builder()
                .model("no-silo-raag")
                .prompt("Just say 'HELLO' in lowercased letters.")
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("hello", response.choices().get(0).text());

        // TODO Test Stream
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    void testFarmWithSunspotsSiloOpenAI() {
        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .build();
        CompletionRequest request = CompletionRequest.builder()
                .model("sunspots-raag")
                .prompt("Who is the author of document 'The Size of the Carrington Event Sunspot Group'? Just reply the name.")
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("Peter Meadows", response.choices().get(0).text());

        // TODO Test Stream
        // TODO Test Rewrite Chat History
    }
}
