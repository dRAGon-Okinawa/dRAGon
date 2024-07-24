package ai.dragon.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.dto.openai.model.OpenAiModel;
import ai.dragon.entity.FarmEntity;
import ai.dragon.repository.FarmRepository;

@SpringBootTest
@ActiveProfiles("test")
public class RaagServiceTest {
    @Autowired
    private RaagService raagService;

    @Autowired
    private FarmRepository farmRepository;

    @BeforeAll
    static void prepare(@Autowired FarmRepository farmRepository) {
        farmRepository.deleteAll();
    }

    @AfterAll
    static void clean(@Autowired FarmRepository farmRepository) {
        farmRepository.deleteAll();
    }

    @Test
    void testListAvailableModels() {
        farmRepository.deleteAll();

        FarmEntity farm = new FarmEntity();
        farm.setUuid(UUID.randomUUID());
        farm.setName("dRAGon Farm #1");
        farm.setRaagIdentifier("dragon-farm-1");
        farmRepository.save(farm);

        List<OpenAiModel> availableModels = raagService.listAvailableModels();
        assertNotNull(availableModels);
        assertNotEquals(0, availableModels.size());
    }
}
