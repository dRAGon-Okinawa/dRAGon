package ai.dragon.util.langchain4j.web.search.searxng;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@Testcontainers
public class SearXNGClientTest {
    private static final Integer SEARXNG_PORT = 8080;

    @Container
    @ClassRule
    @SuppressWarnings({ "rawtypes", "resource" })
    public static GenericContainer searxng = new GenericContainer(DockerImageName.parse("searxng/searxng"))
            .withExposedPorts(SEARXNG_PORT);

    @Test
    public void checkSearXNGAvailability() {
        String address = searxng.getHost();
        Integer port = searxng.getFirstMappedPort();

        assertNotNull(port);
        assertNotNull(address);
    }
}
