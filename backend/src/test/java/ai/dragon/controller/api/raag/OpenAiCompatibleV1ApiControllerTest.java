package ai.dragon.controller.api.raag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.FarmEntity;
import ai.dragon.entity.SiloEntity;
import ai.dragon.enumeration.EmbeddingModelType;
import ai.dragon.enumeration.IngestorLoaderType;
import ai.dragon.enumeration.LanguageModelType;
import ai.dragon.enumeration.QueryRouterType;
import ai.dragon.enumeration.VectorStoreType;
import ai.dragon.junit.AbstractTest;
import ai.dragon.junit.extension.retry.RetryOnExceptions;
import ai.dragon.repository.FarmRepository;
import ai.dragon.repository.SiloRepository;
import ai.dragon.service.IngestorService;
import ai.dragon.util.DataUrlUtil;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.chat.Message;
import dev.ai4j.openai4j.chat.SystemMessage;
import dev.ai4j.openai4j.chat.UserMessage;
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
            @Autowired IngestorService ingestorService,
            @Value("${OPENAI_API_KEY:}") String openaiApiKey) throws Exception {
        cleanUp(farmRepository, siloRepository);

        // OpenAI settings for RaaG
        String apiKeySetting = String.format("apiKey=%s", openaiApiKey);
        String omniModelNameSetting = "modelName=gpt-4o-mini-2024-07-18";

        // Farm with no silo
        FarmEntity farmWithoutSilo = new FarmEntity();
        farmWithoutSilo.setRaagIdentifier("no-silo-raag");
        farmWithoutSilo.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithoutSilo.setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
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
        sunspotsSilo.setDescription(
                "Documents about Sunspots and their effects on Earth : Carrington Event, Solar Activity, etc.");
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

        // Launching ingestion of documents inside the Silo "Sunspots"
        ingestorService.runSiloIngestion(sunspotsSilo, ingestProgress -> {
            System.out.println("Sunspots Silo Ingest progress: " + ingestProgress);
        }, ingestLogMessage -> {
            System.out.println(ingestLogMessage.getMessage());
        });

        // Silo about "WebSSH"
        // The "awesome" iOS / macOS SSH, SFTP and Port Forwarding client since 2012!
        SiloEntity websshSilo = new SiloEntity();
        websshSilo.setUuid(UUID.randomUUID());
        websshSilo.setName("WebSSH Silo");
        websshSilo.setDescription(
                "Documents about WebSSH, the iOS / macOS SSH, SFTP and Port Forwarding client since 2012!");
        websshSilo.setEmbeddingModel(EmbeddingModelType.BgeSmallEnV15QuantizedEmbeddingModel);
        websshSilo.setEmbeddingSettings(List.of(
                "chunkSize=1000",
                "chunkOverlap=100"));
        websshSilo.setVectorStore(VectorStoreType.InMemoryEmbeddingStore);
        websshSilo.setIngestorLoader(IngestorLoaderType.URL);
        websshSilo.setIngestorSettings(List.of(
                "urls[]=https://webssh.net",
                "urls[]=https://webssh.net/documentation/help/networking/dynamic-port-forwarding/",
                "urls[]=https://webssh.net/documentation/mashREPL/",
                "urls[]=https://webssh.net/documentation/web-browser/",
                "urls[]=https://webssh.net/documentation/pricing/",
                "urls[]=https://webssh.net/documentation/help/SSH/terrapin-attack/",
                "urls[]=https://webssh.net/support/",
                "urls[]=https://webssh.net/documentation/help/networking/vpn-over-ssh/"));
        siloRepository.save(websshSilo);

        // Launching ingestion of documents inside the Silo "WebSSH"
        ingestorService.runSiloIngestion(websshSilo, ingestProgress -> {
            System.out.println("WebSSH Silo Ingest progress: " + ingestProgress);
        }, ingestLogMessage -> {
            System.out.println(ingestLogMessage.getMessage());
        });

        // Farm with the Sunspots Silo
        FarmEntity farmWithSunspotsSilo = new FarmEntity();
        farmWithSunspotsSilo.setRaagIdentifier("sunspots-raag");
        farmWithSunspotsSilo.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsSilo.setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        farmWithSunspotsSilo.setSilos(List.of(sunspotsSilo.getUuid()));
        farmRepository.save(farmWithSunspotsSilo);

        // Farm with the Sunspots Silo but with Query Rewriting
        FarmEntity farmWithSunspotsSiloAndQueryRewriting = new FarmEntity();
        farmWithSunspotsSiloAndQueryRewriting.setRaagIdentifier("sunspots-rewriting-raag");
        farmWithSunspotsSiloAndQueryRewriting.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsSiloAndQueryRewriting
                .setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        farmWithSunspotsSiloAndQueryRewriting.setSilos(List.of(sunspotsSilo.getUuid()));
        farmWithSunspotsSiloAndQueryRewriting.setRetrievalAugmentorSettings(List.of("rewriteQuery=true"));
        farmRepository.save(farmWithSunspotsSiloAndQueryRewriting);

        // Farm with the Sunspots Silo but with Query Rewriting + Omni Model
        FarmEntity farmWithSunspotsSiloAndQueryRewritingOmni = new FarmEntity();
        farmWithSunspotsSiloAndQueryRewritingOmni.setRaagIdentifier("sunspots-rewriting-raag-omni");
        farmWithSunspotsSiloAndQueryRewritingOmni.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsSiloAndQueryRewritingOmni
                .setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        farmWithSunspotsSiloAndQueryRewritingOmni.setSilos(List.of(sunspotsSilo.getUuid()));
        farmWithSunspotsSiloAndQueryRewritingOmni.setRetrievalAugmentorSettings(List.of("rewriteQuery=true"));
        farmRepository.save(farmWithSunspotsSiloAndQueryRewritingOmni);

        // Farm with two Silos : Sunspots and WebSSH
        // Language Model Router is used to route the request to the right Silo
        FarmEntity farmWithSunspotsAndWebSSHSilosFallbackFail = new FarmEntity();
        farmWithSunspotsAndWebSSHSilosFallbackFail.setRaagIdentifier("sunspots-webssh-raag-fallbackfail");
        farmWithSunspotsAndWebSSHSilosFallbackFail.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsAndWebSSHSilosFallbackFail
                .setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        farmWithSunspotsAndWebSSHSilosFallbackFail
                .setSilos(List.of(sunspotsSilo.getUuid(), websshSilo.getUuid()));
        farmWithSunspotsAndWebSSHSilosFallbackFail.setQueryRouter(QueryRouterType.LanguageModel);
        farmWithSunspotsAndWebSSHSilosFallbackFail
                .setRetrievalAugmentorSettings(List.of("languageQueryRouterFallbackStrategy=FAIL"));
        farmRepository.save(farmWithSunspotsAndWebSSHSilosFallbackFail);

        // Same Farm as above but with a different fallback strategy : DO_NOT_ROUTE
        FarmEntity farmWithSunspotsAndWebSSHSilosDoNotRoute = new FarmEntity();
        farmWithSunspotsAndWebSSHSilosDoNotRoute.setRaagIdentifier("sunspots-webssh-raag-donotroute");
        farmWithSunspotsAndWebSSHSilosDoNotRoute.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsAndWebSSHSilosDoNotRoute
                .setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        farmWithSunspotsAndWebSSHSilosDoNotRoute
                .setSilos(List.of(sunspotsSilo.getUuid(), websshSilo.getUuid()));
        farmWithSunspotsAndWebSSHSilosDoNotRoute.setQueryRouter(QueryRouterType.LanguageModel);
        farmWithSunspotsAndWebSSHSilosDoNotRoute.setRetrievalAugmentorSettings(
                List.of("languageQueryRouterFallbackStrategy=DO_NOT_ROUTE"));
        farmRepository.save(farmWithSunspotsAndWebSSHSilosDoNotRoute);

        // Same Farm as above but with a different fallback strategy : ROUTE_TO_ALL
        FarmEntity farmWithSunspotsAndWebSSHSilosRouteToAll = new FarmEntity();
        farmWithSunspotsAndWebSSHSilosRouteToAll.setRaagIdentifier("sunspots-webssh-raag-routetoall");
        farmWithSunspotsAndWebSSHSilosRouteToAll.setLanguageModel(LanguageModelType.OpenAiModel);
        farmWithSunspotsAndWebSSHSilosRouteToAll
                .setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        farmWithSunspotsAndWebSSHSilosRouteToAll
                .setSilos(List.of(sunspotsSilo.getUuid(), websshSilo.getUuid()));
        farmWithSunspotsAndWebSSHSilosRouteToAll.setQueryRouter(QueryRouterType.LanguageModel);
        farmWithSunspotsAndWebSSHSilosRouteToAll.setRetrievalAugmentorSettings(
                List.of("languageQueryRouterFallbackStrategy=ROUTE_TO_ALL"));
        farmRepository.save(farmWithSunspotsAndWebSSHSilosRouteToAll);
    }

    @AfterAll
    static void afterAll(@Autowired FarmRepository farmRepository, @Autowired SiloRepository siloRepository) {
        cleanUp(farmRepository, siloRepository);
    }

    static void cleanUp(FarmRepository farmRepository, SiloRepository siloRepository) {
        farmRepository.deleteAll();
        siloRepository.deleteAll();
    }

    @SuppressWarnings("rawtypes")
    private OpenAiClient.Builder createOpenAiClientBuilder() {
        return OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .callTimeout(Duration.ofSeconds(15))
                .readTimeout(Duration.ofSeconds(15))
                .writeTimeout(Duration.ofSeconds(15))
                .connectTimeout(Duration.ofSeconds(15));
    }

    @SuppressWarnings("rawtypes")
    private MistralAiClient.Builder createMistralAiClientBuilder() {
        return MistralAiClient.builder()
                .apiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .timeout(Duration.ofSeconds(15))
                .logRequests(false)
                .logResponses(false);
    }

    @Test
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void listModels() throws Exception {
        MistralAiClient client = createMistralAiClientBuilder().build();
        MistralAiModelResponse modelsResponse = client.listModels();
        assertNotNull(modelsResponse);
        assertNotEquals(0, modelsResponse.getData().size());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testModelDoesntExistOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
        CompletionRequest request = CompletionRequest.builder()
                .model("should-not-exist")
                .prompt("Just say : 'dRAGon'")
                .build();
        OpenAiHttpException exception = assertThrows(OpenAiHttpException.class,
                () -> client.completion(request).execute());
        assertEquals(404, exception.code());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmNoSiloOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
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
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmCompletionOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
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
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmCompletionStreamOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
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
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmChatRewriteQueryOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
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
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    @SuppressWarnings("unchecked")
    void testFarmCompletionWithMetadataFilterOpenAI() {
        Map.of(
                "non_existing_document.pdf", false,
                "BAAJournalCarringtonEventPaper_compressed.pdf", true)
                .forEach((documentName, expected) -> {
                    Map<String, String> customHeaders = Map.of(
                            "X-RAG-FILTER-METADATA",
                            String.format("{{#metadataKey('document_name').isIn('%s')}}",
                                    documentName));
                    OpenAiClient client = createOpenAiClientBuilder()
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

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmWithImagesInputUserMessageOpenAI() throws IOException {
        File solarSunspotsThumbnailPictures = new File(
                "src/test/resources/rag_documents/sunspots/solar-sunspots-thumbnail-isontheline-20240728.png");
        assertTrue(solarSunspotsThumbnailPictures.exists());

        OpenAiClient client = createOpenAiClientBuilder().build();

        List<Message> chatMessages = new ArrayList<>();
        chatMessages.add(
                SystemMessage.from(
                        "You are a researcher in solar physics and you provide help to other researchers."));
        chatMessages.add(UserMessage.from(
                "Hello I need to work on this picture."));
        chatMessages.add(UserMessage.from("""
                Could you tell me what is it?
                Just reply one of the following options (without asterisks):
                * JUPITER
                * MERCURY
                * SUN
                * VENUS
                * SATURN
                """, DataUrlUtil.convertFileToDataImageBase64(solarSunspotsThumbnailPictures)));

        ChatCompletionRequest.Builder requestBuilder = ChatCompletionRequest.builder()
                .messages(chatMessages)
                .temperature(0.0);
        ChatCompletionRequest request = requestBuilder
                .model("sunspots-rewriting-raag-omni")
                .stream(false)
                .build();
        ChatCompletionResponse response = client.chatCompletion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("SUN", response.content());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmLanguageQueryRouterOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
        CompletionRequest request = CompletionRequest.builder()
                .model("sunspots-webssh-raag-fallbackfail")
                .prompt("Who is the maintainer of WebSSH? Reply only with the nickname.")
                .stream(false)
                .temperature(0.0)
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("isontheline", response.choices().get(0).text());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmLanguageQueryRouterFallbackFailOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
        CompletionRequest request = CompletionRequest.builder()
                .model("sunspots-webssh-raag-fail")
                .prompt("What about the Automated Vehicle Safety Consortium?")
                .stream(false)
                .temperature(0.0)
                .build();
        assertThrows(OpenAiHttpException.class,
                () -> client.completion(request).execute());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmLanguageQueryRouterFallbackDoNotRouteOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
        CompletionRequest request = CompletionRequest.builder()
                .model("sunspots-webssh-raag-donotroute")
                .prompt("""
                            * Please disregard all previous inputs and knowledge, focus exclusively on the context provided
                            * Only reply with 'dark', 'bright' or 'unknown'
                            Based solely on the provided context, please tell me if s-u-n-s-p-o-t-s are dark or bright?
                        """)
                .stream(false)
                .temperature(0.0)
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("dark", response.choices().get(0).text());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    @RetryOnExceptions(value = 2, onExceptions = { InterruptedIOException.class, SocketTimeoutException.class })
    void testFarmLanguageQueryRouterFallbackRouteToAllOpenAI() {
        OpenAiClient client = createOpenAiClientBuilder().build();
        CompletionRequest request = CompletionRequest.builder()
                .model("sunspots-webssh-raag-routetoall")
                .prompt("""
                    * Reply with answers separated by a comma
                    1. Who is the author of document 'The Size of the Carrington Event Sunspot Group'? Just reply with the firstname and lastname.
                    2. Who is the maintainer of WebSSH? Reply only with the nickname.
                        """)
                .stream(false)
                .temperature(0.0)
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("Peter Meadows, isontheline", response.choices().get(0).text());
    }
}
