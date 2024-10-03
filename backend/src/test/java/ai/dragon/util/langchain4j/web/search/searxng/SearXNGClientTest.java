package ai.dragon.util.langchain4j.web.search.searxng;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import ai.dragon.junit.AbstractTest;
import ai.dragon.junit.extension.retry.RetryingTest;
import ai.dragon.junit.extension.retry.RetryingTestExtension;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.web.search.WebSearchRequest;
import dev.langchain4j.web.search.WebSearchResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ActiveProfiles("test")
@Testcontainers
@ExtendWith(RetryingTestExtension.class)
public class SearXNGClientTest extends AbstractTest {
    private static final Integer SEARXNG_PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearXNGClientTest.class);

    @Container
    @ClassRule
    @SuppressWarnings({ "rawtypes", "resource", "unchecked" })
    public static GenericContainer searxng = new GenericContainer(DockerImageName.parse("searxng/searxng"))
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

    private String getBaseUrl() {
        String baseUrl = String.format("http://%s:%d", searxng.getHost(), searxng.getMappedPort(SEARXNG_PORT));
        assertNotNull(baseUrl);
        return baseUrl;
    }

    @Test
    public void checkSearXNGAvailability() {
        String address = searxng.getHost();
        Integer port = searxng.getFirstMappedPort();

        assertNotNull(port);
        assertNotNull(address);
    }

    @RetryingTest(3)
    public void testSearXNGUrls() {
        String baseUrl = getBaseUrl();
        assertNotNull(baseUrl);

        OkHttpClient client = new OkHttpClient();
        String[] urlsToCheck = { "/", "/search", "/search?q=SearXNG&format=json" };
        for (String url : urlsToCheck) {
            Request request = new Request.Builder()
                    .url(baseUrl + url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                assertTrue(response.isSuccessful(),
                        String.format("Failed to fetch (http %d) : %s", response.code(), url));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @RetryingTest(3)
    @EnabledIf("canRunSearXNGRelatedTests")
    public void testSearXNGClientResults() {
        SearXNGClient client = SearXNGClient
                .builder()
                .baseUrl(getBaseUrl())
                .build();
        SearXNGSearchRequest request = SearXNGSearchRequest
                .builder()
                .q("United State of America")
                .build();
        SearXNGResponse response = client.search(request);
        assertNotNull(response, "Response is null");
        assertNotNull(response.getAnswers(), "Answers is null");
        assertNotEquals(0, response.getAnswers().size(), "Answers is empty");
        assertNotNull(response.getInfoboxes(), "Infoboxes is null");
        assertNotEquals(0, response.getInfoboxes().size(), "Infoboxes is empty");
        assertNotEquals(0, response.getInfoboxes().get(0).getAttributes().size(), "Infobox attributes is empty");
        assertNotNull(response.getResults(), "Results is null");
        assertNotEquals(0, response.getResults().size(), "Results is empty");
        assertNotNull(response.getSuggestions(), "Suggestions is null");
        assertNotEquals(0, response.getSuggestions().size(), "Suggestions is empty");
    }

    @RetryingTest(3)
    @EnabledIf("canRunSearXNGRelatedTests")
    public void testSearXNGSearchEngine() {
        SearXNGWebSearchEngine searchEngine = SearXNGWebSearchEngine
                .builder()
                .baseUrl(getBaseUrl())
                .timeout(Duration.ofSeconds(10))
                .appendInfoboxes(true)
                .appendSuggestions(true)
                .appendAnswers(true)
                .build();
        WebSearchRequest request = WebSearchRequest
                .builder()
                .searchTerms("United State of America")
                .maxResults(10)
                .build();
        WebSearchResults results = searchEngine.search(request);
        assertNotNull(results);
        assertNotNull(results.toTextSegments());
        assertNotEquals(0, results.toTextSegments().size());
    }

    @RetryingTest(3)
    @EnabledIf("canRunSearXNGRelatedTests")
    public void testSearXNGSearchEngineMaxResults() {
        SearXNGWebSearchEngine searchEngine = SearXNGWebSearchEngine
                .builder()
                .baseUrl(getBaseUrl())
                .timeout(Duration.ofSeconds(10))
                .build();
        WebSearchRequest request = WebSearchRequest
                .builder()
                .searchTerms("United State of America")
                .maxResults(3)
                .build();
        WebSearchResults results = searchEngine.search(request);
        assertNotNull(results);
        assertEquals(3, results.results().size());
    }

    @RetryingTest(3)
    @EnabledIf("canRunSearXNGRelatedTests")
    public void testSearXNGWebSearchContentRetriever() {
        SearXNGWebSearchEngine searchEngine = SearXNGWebSearchEngine
                .builder()
                .baseUrl(getBaseUrl())
                .timeout(Duration.ofSeconds(10))
                .appendInfoboxes(true)
                .appendSuggestions(true)
                .appendAnswers(true)
                .build();
        ContentRetriever webSearchContentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(searchEngine)
                .maxResults(3)
                .build();
        List<Content> results = webSearchContentRetriever.retrieve(Query.from("United State of America"));
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
