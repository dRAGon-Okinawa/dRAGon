package ai.dragon.controller.api.raag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
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
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
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
        String modelNameSetting = "modelName=gpt-4o-mini";

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
                "pathMatcher=glob:**.{pdf,doc,docx,ppt,pptx}"));
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

        // Farm with the Sunspots Silo but with Query Rewriting
        FarmEntity farmWithSunspotsSiloAndQueryRewriting = new FarmEntity();
        farmWithSunspotsSiloAndQueryRewriting.setRaagIdentifier("sunspots-rewriting-raag");
        farmWithSunspotsSiloAndQueryRewriting.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsSiloAndQueryRewriting.setLanguageModelSettings(List.of(apiKeySetting, modelNameSetting));
        farmWithSunspotsSiloAndQueryRewriting.setSilos(List.of(sunspotsSilo.getUuid()));
        farmWithSunspotsSiloAndQueryRewriting.setRetrievalAugmentorSettings(List.of("rewriteQuery=true"));
        farmRepository.save(farmWithSunspotsSiloAndQueryRewriting);
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
                .temperature(0.0)
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("hello", response.choices().get(0).text());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    void testFarmCompletionOpenAI() {
        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .build();
        CompletionRequest request = CompletionRequest.builder()
                .model("sunspots-raag")
                .prompt("Who is the author of document 'The Size of the Carrington Event Sunspot Group'? Just reply with the firstname and lastname.")
                .stream(false)
                .temperature(0.0)
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("Peter Meadows", response.choices().get(0).text());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    void testFarmCompletionStreamOpenAI() {
        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .build();
        CompletionRequest request = CompletionRequest.builder()
                .model("sunspots-raag")
                .prompt("""
                        Who is the author of document 'Sunspots, unemployment, and recessions, or Can the solar activity cycle shape the business cycle?'?
                        Just reply with the firstname and lastname.
                        """)
                .stream(true)
                .temperature(0.0)
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("Mikhail Gorbanev", response.choices().get(0).text());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    void testFarmChatRewriteQueryOpenAI() {
        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .build();
        ChatCompletionRequest.Builder requestBuilder = ChatCompletionRequest.builder()
                .addSystemMessage(
                        "You are a researcher in solar physics and you provide help to other researchers.")
                .addUserMessage(
                        "Hello, I am looking for the author of the document 'The Size of the Carrington Event Sunspot Group'.")
                .addUserMessage(
                        """
                                Can you help me?
                                * Just use the context of this message and nothing else to reply.
                                * If the information is not provided, just say 'I do not know!'
                                * Just reply with the firstname and lastname.
                                    """)
                .temperature(0.0);

        for (int i = 0; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
                ChatCompletionRequest request = requestBuilder
                        .model(i == 0 ? "sunspots-raag" : "sunspots-rewriting-raag")
                        .stream(j == 1)
                        .build();
                ChatCompletionResponse response = client.chatCompletion(request).execute();
                assertNotNull(response);
                assertNotNull(response.choices());
                assertNotEquals(0, response.choices().size());
                assertEquals(i == 0 ? "I do not know!" : "Peter Meadows", response.content());
            }
        }
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @SuppressWarnings("unchecked")
    void testFarmCompletionWithMetadataFilterOpenAI() {
        Map.of(
                "non_existing_document.pdf", false,
                "BAAJournalCarringtonEventPaper_compressed.pdf", true)
                .forEach((documentName, expected) -> {
                    Map<String, String> customHeaders = Map.of(
                            "X-RAG-FILTER-METADATA",
                            String.format("{{#metadataKey('document_name').isIn('%s')}}", documentName));
                    OpenAiClient client = OpenAiClient.builder()
                            .openAiApiKey("TODO_PUT_KEY_HERE")
                            .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                            .customHeaders(customHeaders)
                            .build();
                    CompletionRequest request = CompletionRequest.builder()
                            .model("sunspots-raag")
                            .prompt("""
                                    Who is the author of document 'The Size of the Carrington Event Sunspot Group'?
                                    Just reply with the firstname and lastname.
                                    """)
                            .stream(false)
                            .temperature(0.0)
                            .build();
                    CompletionResponse response = client.completion(request).execute();
                    assertNotNull(response);
                    assertNotNull(response.choices());
                    assertNotEquals(0, response.choices().size());
                    if (expected) {
                        assertEquals("Peter Meadows", response.choices().get(0).text());
                    } else {
                        assertNotEquals("Peter Meadows", response.choices().get(0).text());
                    }
                });
    }
}
