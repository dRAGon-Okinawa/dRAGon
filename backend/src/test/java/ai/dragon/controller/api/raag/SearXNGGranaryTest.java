package ai.dragon.controller.api.raag;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import ai.dragon.entity.FarmEntity;
import ai.dragon.entity.GranaryEntity;
import ai.dragon.enumeration.GranaryEngineType;
import ai.dragon.enumeration.LanguageModelType;
import ai.dragon.enumeration.QueryRouterType;
import ai.dragon.junit.AbstractTest;
import ai.dragon.repository.FarmRepository;
import ai.dragon.repository.GranaryRepository;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class SearXNGGranaryTest extends AbstractTest {
    private static final Integer SEARXNG_PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearXNGGranaryTest.class);

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private GranaryRepository granaryRepository;

    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;

    @Container
    @ClassRule
    @SuppressWarnings({ "resource" })
    public static GenericContainer<?> searxng = new GenericContainer<>(DockerImageName.parse("searxng/searxng"))
            .withStartupTimeout(Duration.ofSeconds(60))
            .withExposedPorts(SEARXNG_PORT)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("/searxng/config/settings.yml"),
                    "/etc/searxng/settings.yml")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("/searxng/config/limiter.toml"),
                    "/etc/searxng/limiter.toml")
            .withEnv("SEARXNG_SECRET", UUID.randomUUID().toString())
            .withLogConsumer(new Slf4jLogConsumer(LOGGER))
            .waitingFor(Wait.forHttp("/search").forStatusCode(200));

    @LocalServerPort
    private int serverPort;

    @BeforeAll
    static void beforeAll(@Autowired FarmRepository farmRepository,
            @Autowired GranaryRepository granaryRepository,
            @Value("${OPENAI_API_KEY:}") String openaiApiKey) throws Exception {
        cleanUp(farmRepository, granaryRepository);
    }

    @AfterAll
    static void afterAll(@Autowired FarmRepository farmRepository, @Autowired GranaryRepository granaryRepository) {
        cleanUp(farmRepository, granaryRepository);
    }

    static void cleanUp(FarmRepository farmRepository, GranaryRepository granaryRepository) {
        farmRepository.deleteAll();
        granaryRepository.deleteAll();
    }

    private String getBaseUrl() {
        String baseUrl = String.format("http://%s:%d", searxng.getHost(), searxng.getMappedPort(SEARXNG_PORT));
        assertNotNull(baseUrl);
        return baseUrl;
    }

    @SuppressWarnings("rawtypes")
    private OpenAiClient.Builder createOpenAiClientBuilder() {
        return OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_DRAGON_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .callTimeout(Duration.ofSeconds(15))
                .readTimeout(Duration.ofSeconds(15))
                .writeTimeout(Duration.ofSeconds(15))
                .connectTimeout(Duration.ofSeconds(15));
    }

    @Test
    void testSearRaaG() {
        // OpenAI settings for RaaG
        String apiKeySetting = String.format("apiKey=%s", openaiApiKey);
        String omniModelNameSetting = "modelName=gpt-4o-mini-2024-07-18";

        // Granary "WebSearchEngine" with SearXNG
        GranaryEntity xngGranary = new GranaryEntity();
        xngGranary.setUuid(UUID.randomUUID());
        xngGranary.setName("SearXNG Granary");
        xngGranary.setDescription("If user want to search over the web.");
        xngGranary.setEngineType(GranaryEngineType.WebSearchEngine);
        xngGranary.setEngineSettings(List.of(
                "engine=SearXNG",
                "baseUrl=" + getBaseUrl(),
                "maxResults=10",
                "appendInfoboxes=true",
                "appendSuggestions=true",
                "appendAnswers=true"));
        granaryRepository.save(xngGranary);

        // Empty Farm
        FarmEntity emptyFarm = new FarmEntity();
        emptyFarm.setRaagIdentifier("empty-farm");
        emptyFarm.setLanguageModel(LanguageModelType.OpenAiModel);
        emptyFarm.setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        farmRepository.save(emptyFarm);

        // Farm with the SearXNG Granary
        // Language Model Router is used to route the request to the right Granary
        FarmEntity xngFarm = new FarmEntity();
        xngFarm.setRaagIdentifier("searxng-raag-fallbackdonotroute");
        xngFarm.setLanguageModel(LanguageModelType.OpenAiModel);
        xngFarm.setLanguageModelSettings(List.of(apiKeySetting, omniModelNameSetting));
        xngFarm.setGranaries(List.of(xngGranary.getUuid()));
        xngFarm.setQueryRouter(QueryRouterType.LANGUAGE_MODEL);
        xngFarm.setRetrievalAugmentorSettings(List.of(
                "languageQueryRouterFallbackStrategy=DO_NOT_ROUTE"));
        farmRepository.save(xngFarm);

        String[] farmsToCheck = { "empty-farm", "searxng-raag-fallbackdonotroute" };
        for (String farmId : farmsToCheck) {
            CompletionRequest request = CompletionRequest.builder()
                    .model(farmId)
                    .prompt("site:www.petermeadows.com version Helio Viewer")
                    .stream(false)
                    .temperature(0.0)
                    .build();
            CompletionResponse response = createOpenAiClientBuilder().build().completion(request).execute();
            assertNotNull(response);
            assertNotNull(response.choices());
            assertNotEquals(0, response.choices().size());
            boolean contains = response.choices().get(0).text().contains("Peter Meadows");
            if ("empty-farm".equals(farmId)) {
                assertTrue(!contains);
            } else {
                assertTrue(contains);
            }
        }
    }
}
