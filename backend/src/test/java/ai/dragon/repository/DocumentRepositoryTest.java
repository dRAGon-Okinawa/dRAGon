package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.repository.Cursor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.DocumentEntity;

@SpringBootTest
@ActiveProfiles("test")
public class DocumentRepositoryTest {
    @Autowired
    private DocumentRepository documentRepository;

    @Test
    void insertDocuments() {
        documentRepository.deleteAll();
        String documentName = "Document #1";

        DocumentEntity document = DocumentEntity
                .builder()
                .siloIdentifier(UUID.randomUUID())
                .name(documentName)
                .location("https://dragon.okinawa")
                .build();
        documentRepository.save(document);

        DocumentEntity retrievedDocument = documentRepository.getByUuid(document.getUuid()).orElseThrow();
        assertNotNull(documentRepository.getByUuid(retrievedDocument.getUuid()));
        assertEquals(documentName, retrievedDocument.getName());
    }

    @Test
    void findDocumentsForSilo() {
        documentRepository.deleteAll();
        int nbDocumentsToInsert = 3;
        UUID siloIdentifier = UUID.randomUUID();

        for (int i = 0; i < nbDocumentsToInsert; i++) {
            documentRepository.save(DocumentEntity
                    .builder()
                    .siloIdentifier(siloIdentifier)
                    .name(String.format("Document %d", i))
                    .location(String.format("https://dragon.okinawa/document-%d", i))
                    .build());
        }

        assertEquals(nbDocumentsToInsert, documentRepository.countAll());

        Cursor<DocumentEntity> results = documentRepository.findWithFilter(FluentFilter
                .where("siloIdentifier")
                .eq(siloIdentifier.toString()),
                FindOptions.orderBy("name", SortOrder.Descending));
        assertNotNull(results);
        assertEquals(nbDocumentsToInsert, results.size());

        results.forEach(documentRetrieved -> {
            assertEquals(siloIdentifier.toString(), documentRetrieved.getSiloIdentifier().toString());
        });
        assertEquals(String.format("Document %d", nbDocumentsToInsert - 1), results.firstOrNull().getName());
    }
}
