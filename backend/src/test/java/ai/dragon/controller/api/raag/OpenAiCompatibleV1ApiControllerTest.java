package ai.dragon.controller.api.raag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;

import ai.dragon.entity.FarmEntity;
import ai.dragon.repository.FarmRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenAiCompatibleV1ApiControllerTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private FarmRepository farmRepository;

    @Test
    void listModels() throws Exception {
        farmRepository.deleteAll();
        FarmEntity farm = new FarmEntity();
        farm.setRaagIdentifier("awesome-raag");
        farmRepository.save(farm);
        OpenAiService service = new OpenAiService("TODO_PUT_KEY_HERE",
                String.format("http://localhost:%d/api/raag/v1/", serverPort));
        List<Model> models = service.listModels();
        assertFalse(models.isEmpty());
        assertEquals(1, models.size());
    }
}