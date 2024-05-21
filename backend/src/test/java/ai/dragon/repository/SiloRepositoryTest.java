package ai.dragon.repository;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.model.SiloEntity;

@SpringBootTest
@ActiveProfiles("test")
public class SiloRepositoryTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SiloRepository siloRepository;

    @Test
    void insertSilos() {
        SiloEntity silo = new SiloEntity();
        silo.setUuid(UUID.randomUUID());
        silo.setName("Test Silo2");
        siloRepository.save(silo);
    }

    @Test
    void findSilos() {
        siloRepository.find().forEach(silo -> {
            logger.info("Silo: " + silo.getName());
        });
    }
}
