package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.dizitart.no2.repository.Cursor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.SiloEntity;

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

        SiloEntity retrievedSilo = siloRepository.getByUuid(silo.getUuid()).orElseThrow();
        assertNotNull(siloRepository.getByUuid(retrievedSilo.getUuid()));
        assertEquals(siloName, retrievedSilo.getName());
    }

    @Test
    void findAllSilos() {
        siloRepository.deleteAll();
        int nbSilosToInsert = 3;

        for (int i = 0; i < nbSilosToInsert; i++) {
            siloRepository.save(new SiloEntity());
        }

        assertEquals(nbSilosToInsert, siloRepository.countAll());

        siloRepository.find().forEach(silo -> {
            assertNotNull(silo.getUuid());
        });
    }

    @Test
    void findSilosByName() {
        siloRepository.deleteAll();
        int nbSilosToInsert = 3;

        for (int i = 0; i < nbSilosToInsert; i++) {
            SiloEntity silo = new SiloEntity();
            silo.setName(String.format("Silo %d", i));
            siloRepository.save(silo);
        }

        assertEquals(nbSilosToInsert, siloRepository.countAll());

        Cursor<SiloEntity> cursor = siloRepository.findByFieldValue("name", "Silo 1");
        assertEquals(1, cursor.size());
    }

    @Test
    void deleteSilos() {
        siloRepository.deleteAll();
        int nbSilosToInsert = 3;

        for (int i = 0; i < nbSilosToInsert; i++) {
            siloRepository.save(new SiloEntity());
        }

        assertEquals(nbSilosToInsert, siloRepository.countAll());

        siloRepository.find().forEach(silo -> {
            siloRepository.delete(silo.getUuid());
        });

        assertEquals(0, siloRepository.countAll());
    }
}
