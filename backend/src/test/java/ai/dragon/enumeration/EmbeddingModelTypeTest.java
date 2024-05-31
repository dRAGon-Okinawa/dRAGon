package ai.dragon.enumeration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.job.silo.embedding.EmbeddingModelDefinition;

@SpringBootTest
@ActiveProfiles("test")
public class EmbeddingModelTypeTest {
    @Test
    void testModelsExist() throws ClassNotFoundException {
        EmbeddingModelType[] models = EmbeddingModelType.values();
        for (EmbeddingModelType model : models) {
            EmbeddingModelDefinition embeddingModelDefinition = model.getModelDefinition();
            assertNotNull(Class.forName(embeddingModelDefinition.getEmbeddingModelClassName()));
            assertNotNull(embeddingModelDefinition.getEmbeddingModelClassName());
            assertNotNull(embeddingModelDefinition.getProviderType());
            assertNotNull(embeddingModelDefinition.getDimensions());
            assertNotEquals(0, embeddingModelDefinition.getDimensions());
            assertNotNull(embeddingModelDefinition.getMaxTokens());
            assertNotEquals(0, embeddingModelDefinition.getMaxTokens());
            assertNotNull(embeddingModelDefinition.getLanguages());
            assertNotEquals(0, embeddingModelDefinition.getLanguages().size());
        }
    }
}
