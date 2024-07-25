package ai.dragon.controller.api.raag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;

import ai.dragon.entity.FarmEntity;
import ai.dragon.enumeration.LanguageModelType;
import ai.dragon.repository.FarmRepository;
import ai.dragon.test.AbstractTest;

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

        OpenAiService service = new OpenAiService("TODO_PUT_KEY_HERE",
                String.format("http://localhost:%d/api/raag/v1/", serverPort));
        List<Model> models = service.listModels();
        assertFalse(models.isEmpty());
        assertEquals(1, models.size());
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

        OpenAiService service = new OpenAiService("TODO_PUT_KEY_HERE",
                String.format("http://localhost:%d/api/raag/v1/", serverPort));
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Just say 'HELLO' in lowercased letters.")
                .model("dragon-raag")
                .echo(true)
                .build();
        List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
        assertNotNull(choices);
        assertNotEquals(0, choices.size());
        assertEquals("hello", choices.get(0).getText());
    }
}
