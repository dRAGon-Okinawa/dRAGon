package ai.dragon.enumeration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ProviderTypeTest {
    @Test
    void testModelsExist() {
        ProviderType[] models = ProviderType.values();
        for (ProviderType model : models) {
            assertNotNull(ProviderType.fromString(model.toString()));
        }
    }
}
