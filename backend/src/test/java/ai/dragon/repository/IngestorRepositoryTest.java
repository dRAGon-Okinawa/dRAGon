package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.dizitart.no2.repository.Cursor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.IngestorEntity;

@SpringBootTest
@ActiveProfiles("test")
public class IngestorRepositoryTest {
    @Autowired
    private IngestorRepository ingestorRepository;

    @Test
    void insertIngestors() {
        ingestorRepository.deleteAll();
        String ingestorName = "dRAGon Ingestor #1";

        IngestorEntity ingestor = new IngestorEntity();
        ingestor.setUuid(UUID.randomUUID());
        ingestor.setName(ingestorName);
        ingestorRepository.save(ingestor);

        IngestorEntity retrievedIngestor = ingestorRepository.getByUuid(ingestor.getUuid());
        assertNotNull(ingestorRepository.getByUuid(retrievedIngestor.getUuid()));
        assertEquals(ingestorName, retrievedIngestor.getName());
    }

    @Test
    void findAllIngestors() {
        ingestorRepository.deleteAll();
        int nbIngestorsToInsert = 3;

        for (int i = 0; i < nbIngestorsToInsert; i++) {
            ingestorRepository.save(new IngestorEntity());
        }

        assertEquals(nbIngestorsToInsert, ingestorRepository.countAll());

        ingestorRepository.find().forEach(ingestor -> {
            assertNotNull(ingestor.getUuid());
        });
    }

    @Test
    void findIngestorsByName() {
        ingestorRepository.deleteAll();
        int nbIngestorsToInsert = 3;

        for (int i = 0; i < nbIngestorsToInsert; i++) {
            IngestorEntity ingestor = new IngestorEntity();
            ingestor.setName(String.format("Ingestor %d", i));
            ingestorRepository.save(ingestor);
        }

        assertEquals(nbIngestorsToInsert, ingestorRepository.countAll());

        Cursor<IngestorEntity> cursor = ingestorRepository.findByFieldValue("name", "Ingestor 1");
        assertEquals(1, cursor.size());
    }

    @Test
    void deleteIngestors() {
        ingestorRepository.deleteAll();
        int nbIngestorsToInsert = 3;

        for (int i = 0; i < nbIngestorsToInsert; i++) {
            ingestorRepository.save(new IngestorEntity());
        }

        assertEquals(nbIngestorsToInsert, ingestorRepository.countAll());

        ingestorRepository.find().forEach(ingestor -> {
            ingestorRepository.delete(ingestor.getUuid());
        });

        assertEquals(0, ingestorRepository.countAll());
    }
}
