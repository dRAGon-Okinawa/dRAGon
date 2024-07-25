package ai.dragon.controller.api.raag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.FarmEntity;
import ai.dragon.enumeration.LanguageModelType;
import ai.dragon.repository.FarmRepository;
import ai.dragon.test.AbstractTest;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.langchain4j.model.mistralai.internal.api.MistralAiModelResponse;
import dev.langchain4j.model.mistralai.internal.client.MistralAiClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenAiCompatibleV1ApiControllerTest extends AbstractTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private FarmRepository farmRepository;

    @BeforeAll
    static void beforeAll(@Autowired FarmRepository farmRepository) {
        farmRepository.deleteAll();
    }

    @AfterAll
    static void afterAll(@Autowired FarmRepository farmRepository) {
        farmRepository.deleteAll();
    }

    @BeforeEach
    void beforeEach(@Autowired FarmRepository farmRepository) {
        farmRepository.deleteAll();
    }

    @Test
    void listModels() throws Exception {
        FarmEntity farm = new FarmEntity();
        farm.setRaagIdentifier("awesome-raag");
        farmRepository.save(farm);

        MistralAiClient client = MistralAiClient.builder()
                .apiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .timeout(Duration.ofSeconds(10))
                .logRequests(false)
                .logResponses(false)
                .build();
        MistralAiModelResponse modelsResponse = client.listModels();
        assertNotNull(modelsResponse);
        assertEquals(1, modelsResponse.getData().size());
        assertEquals(farm.getRaagIdentifier(), modelsResponse.getData().get(0).getId());
    }

    @Test
    @EnabledIf("canRunOpenAiRelatedTests")
    void testFarmNoSiloOpenAI() {
        String apiKeySetting = String.format("apiKey=%s", System.getenv("OPENAI_API_KEY"));
        String modelNameSetting = "modelName=gpt-4o";

        FarmEntity farm = new FarmEntity();
        farm.setRaagIdentifier("dragon-raag");
        farm.setLanguageModel(LanguageModelType.OpenAiModel);
        farm.setLanguageModelSettings(List.of(apiKeySetting, modelNameSetting));
        farmRepository.save(farm);

        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey("TODO_PUT_KEY_HERE")
                .baseUrl(String.format("http://localhost:%d/api/raag/v1/", serverPort))
                .build();
        CompletionRequest request = CompletionRequest.builder()
                .model("dragon-raag")
                .prompt("Just say 'HELLO' in lowercased letters.")
                .build();
        CompletionResponse response = client.completion(request).execute();
        assertNotNull(response);
        assertNotNull(response.choices());
        assertNotEquals(0, response.choices().size());
        assertEquals("hello", response.choices().get(0).text());
    }
}
