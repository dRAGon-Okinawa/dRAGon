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
        provider.setType(ProviderType.OPENAI);
        providerRepository.save(provider);

        ProviderEntity retrievedProvider = providerRepository.getByUuid(provider.getUuid());
        assertNotNull(providerRepository.getByUuid(retrievedProvider.getUuid()));
        assertEquals(providerName, retrievedProvider.getName());
    }

    @Test
    void findAllProviders() {
        providerRepository.deleteAll();
        int nbProvidersToInsert = 3;

        for (int i = 0; i < nbProvidersToInsert; i++) {
            ProviderEntity provider = new ProviderEntity();
            provider.setName(String.format("Provider %d", i));
            provider.setType(ProviderType.OPENAI);
            providerRepository.save(provider);
        }

        assertEquals(nbProvidersToInsert, providerRepository.countAll());

        providerRepository.find().forEach(provider -> {
            assertNotNull(provider.getUuid());
        });
    }

    @Test
    void findProvidersByName() {
        providerRepository.deleteAll();
        int nbProvidersToInsert = 3;

        for (int i = 0; i < nbProvidersToInsert; i++) {
            ProviderEntity provider = new ProviderEntity();
            provider.setName(String.format("Provider %d", i));
            provider.setType(ProviderType.OPENAI);
            providerRepository.save(provider);
        }

        assertEquals(nbProvidersToInsert, providerRepository.countAll());

        Cursor<ProviderEntity> cursor = providerRepository.findByFieldValue("name", "Provider 1");
        assertEquals(1, cursor.size());
    }

    @Test
    void deleteProviders() {
        providerRepository.deleteAll();
        int nbProvidersToInsert = 3;

        for (int i = 0; i < nbProvidersToInsert; i++) {
            ProviderEntity provider = new ProviderEntity();
            provider.setType(ProviderType.OPENAI);
            providerRepository.save(provider);
        }

        assertEquals(nbProvidersToInsert, providerRepository.countAll());

        providerRepository.find().forEach(provider -> {
            providerRepository.delete(provider.getUuid());
        });

        assertEquals(0, providerRepository.countAll());
    }
}
