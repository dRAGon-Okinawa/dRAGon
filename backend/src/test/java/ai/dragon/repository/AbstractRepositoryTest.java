package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.SiloEntity;

@SpringBootTest
@ActiveProfiles("test")
public class AbstractRepositoryTest {
    @Autowired
    private SiloRepository siloRepository;

    @Test
    void rollbackSilos() throws Exception {
        // No transaction insert :
        siloRepository.deleteAll();
        SiloEntity silo = new SiloEntity();
        silo.setUuid(UUID.randomUUID());
        silo.setName("dRAGon is awesome");
        siloRepository.save(silo);
        SiloEntity retrievedSilo = siloRepository.getByUuid(silo.getUuid()).orElseThrow();
        assertNotNull(siloRepository.getByUuid(retrievedSilo.getUuid()));
        assertEquals(1, siloRepository.countAll());

        // Transaction insert :
        siloRepository.deleteAll();
        SiloEntity transactionSilo = new SiloEntity();
        transactionSilo.setUuid(UUID.randomUUID());
        transactionSilo.setName("dRAGon is awesome");
        siloRepository.executeTransaction(transactionRepository -> {
            transactionRepository.save(transactionSilo);
        });
        SiloEntity retrievedTransactionSilo = siloRepository.getByUuid(transactionSilo.getUuid()).orElseThrow();
        assertNotNull(siloRepository.getByUuid(retrievedTransactionSilo.getUuid()));
        assertEquals(1, siloRepository.countAll());

        // Transaction rollback :
        siloRepository.deleteAll();
        SiloEntity transactionRollbackSilo = new SiloEntity();
        transactionRollbackSilo.setUuid(UUID.randomUUID());
        transactionRollbackSilo.setName("dRAGon is awesome");
        Exception exception = assertThrows(Exception.class, () -> {
            siloRepository.queryTransaction(transactionRepository -> {
                transactionRepository.save(transactionRollbackSilo);
                throw new Exception("Rollback");
            });
        });
        assertTrue(exception.getMessage().contains("Rollback"));
        SiloEntity retrievedTransactionRollbackSilo = siloRepository.getByUuid(transactionRollbackSilo.getUuid())
                .orElse(null);
        assertNull(retrievedTransactionRollbackSilo);
        assertEquals(0, siloRepository.countAll());

        // No transaction insert again :
        siloRepository.deleteAll();
        SiloEntity siloNoTransaction = new SiloEntity();
        siloNoTransaction.setUuid(UUID.randomUUID());
        siloNoTransaction.setName("dRAGon is awesome");
        siloRepository.save(siloNoTransaction);
        SiloEntity retrievedSiloNoTransaction = siloRepository.getByUuid(siloNoTransaction.getUuid()).orElseThrow();
        assertNotNull(siloRepository.getByUuid(retrievedSiloNoTransaction.getUuid()));
        assertEquals(1, siloRepository.countAll());
    }
}
