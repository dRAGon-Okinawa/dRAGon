package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.dizitart.no2.repository.Cursor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.ProviderEntity;
import ai.dragon.enumeration.ProviderType;

@SpringBootTest
@ActiveProfiles("test")
public class ProviderRepositoryTest {
    @Autowired
    private ProviderRepository providerRepository;

    @Test
    void insertProviders() {
        providerRepository.deleteAll();
        String providerName = "dRAGon Provider #1";

        ProviderEntity provider = new ProviderEntity();
        provider.setUuid(UUID.randomUUID());
        provider.setName(providerName);
        provider.setType(ProviderType.ONNX);
        providerRepository.save(provider);

        ProviderEntity retrievedProvider = providerRepository.getByUuid(provider.getUuid()).orElseThrow();
        assertNotNull(providerRepository.getByUuid(retrievedProvider.getUuid()));
        assertEquals(providerName, retrievedProvider.getName());
    }

    @Test
    void findAllProviders() {
        providerRepository.deleteAll();
        ProviderEntity provider = new ProviderEntity();
        provider.setName("ONNX Provider");
        provider.setType(ProviderType.ONNX);
        providerRepository.save(provider);
        assertEquals(1, providerRepository.countAll());

        providerRepository.find().forEach(providerFetched -> {
            assertNotNull(providerFetched.getUuid());
        });
    }

    @Test
    void findProvidersByName() {
        providerRepository.deleteAll();
        ProviderEntity provider = new ProviderEntity();
        provider.setName("OpenAI Provider");
        provider.setType(ProviderType.OpenAI);
        providerRepository.save(provider);

        Cursor<ProviderEntity> cursor = providerRepository.findByFieldValue("name", "OpenAI Provider");
        assertEquals(1, cursor.size());
    }

    @Test
    void deleteProviders() {
        providerRepository.deleteAll();
        ProviderEntity provider = new ProviderEntity();
        provider.setName("OpenAI Provider");
        provider.setType(ProviderType.OpenAI);
        providerRepository.save(provider);

        assertEquals(1, providerRepository.countAll());

        providerRepository.find().forEach(providerFetched -> {
            providerRepository.delete(providerFetched.getUuid());
        });

        assertEquals(0, providerRepository.countAll());
    }
}
