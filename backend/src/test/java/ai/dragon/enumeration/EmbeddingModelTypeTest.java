package ai.dragon.enumeration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EmbeddingModelTypeTest {
    @Test
    void testModelsExist() throws ClassNotFoundException {
        EmbeddingModelType[] models = EmbeddingModelType.values();
        for (EmbeddingModelType model : models) {
            EmbeddingModelDefinition embeddingModelDefinition = model.getModelDefinition();
            assertNotNull(Class.forName(embeddingModelDefinition.embeddingModelClassName()));
            assertNotNull(embeddingModelDefinition.embeddingModelClassName());
            assertNotNull(embeddingModelDefinition.providerType());
            assertNotNull(embeddingModelDefinition.dimensions());
            assertNotEquals(0, embeddingModelDefinition.dimensions());
            assertNotNull(embeddingModelDefinition.maxTokens());
            assertNotEquals(0, embeddingModelDefinition.maxTokens());
            assertNotNull(embeddingModelDefinition.languages());
            assertNotEquals(0, embeddingModelDefinition.languages().size());
        }
    }
}
