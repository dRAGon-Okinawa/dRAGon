package ai.dragon.util.langchain4j.web.search.searxng;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ActiveProfiles("test")
@Testcontainers
public class SearXNGClientTest {
    private static final Integer SEARXNG_PORT = 8080;

    @Container
    @ClassRule
    @SuppressWarnings({ "rawtypes", "resource" })
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

    @Test
    public void testSearXNGUrls() {
        String baseUrl = getBaseUrl();
        assertNotNull(baseUrl);

        OkHttpClient client = new OkHttpClient();
        String[] urlsToCheck = { "/", "/search" };
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

    @Test
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
        assertNotNull(response);
        assertNotNull(response.getAnswers());
        assertNotEquals(0, response.getAnswers().size());
        assertNotNull(response.getInfoboxes());
        assertNotEquals(0, response.getInfoboxes().size());
        assertNotEquals(0, response.getInfoboxes().get(0).getAttributes().size());
        assertNotNull(response.getResults());
        assertNotEquals(0, response.getResults().size());
        assertNotNull(response.getSuggestions());
        assertNotEquals(0, response.getSuggestions().size());
    }
}
