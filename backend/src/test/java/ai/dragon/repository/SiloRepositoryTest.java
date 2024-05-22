package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.model.SiloEntity;

@SpringBootTest
@ActiveProfiles("test")
public class SiloRepositoryTest {
    @Autowired
    private SiloRepository siloRepository;

    @Test
    void insertSilos() {
        siloRepository.deleteAll();
        String siloName = "dRAGon Silo #1";

        SiloEntity silo = new SiloEntity();
        silo.setUuid(UUID.randomUUID());
        silo.setName(siloName);
        siloRepository.save(silo);

        SiloEntity retrievedSilo = siloRepository.getByUuid(silo.getUuid());
        assertNotNull(siloRepository.getByUuid(retrievedSilo.getUuid()));
        assertEquals(siloName, retrievedSilo.getName());
    }

    @Test
    void findSilos() {
        siloRepository.deleteAll();
        int nbSilosToInsert = 3;

        for(int i=0; i<nbSilosToInsert; i++) {
            siloRepository.save(new SiloEntity());
        }

        assertEquals(nbSilosToInsert, siloRepository.countAll());

        siloRepository.find().forEach(silo -> {
            assertNotNull(silo.getUuid());
        });
    }
}
