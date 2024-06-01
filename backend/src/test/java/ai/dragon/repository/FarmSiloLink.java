package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.FarmEntity;
import ai.dragon.entity.SiloEntity;

@SpringBootTest
@ActiveProfiles("test")
public class FarmSiloLink {
    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private SiloRepository siloRepository;

    @Test
    void siloAssignementRemove() throws Exception {
        SiloEntity silo = new SiloEntity();
        siloRepository.save(silo);

        FarmEntity farm = new FarmEntity();
        farm.setSilos(Collections.singletonList(silo.getUuid()));
        farmRepository.save(farm);

        farm = farmRepository.getByUuid(farm.getUuid()).orElseThrow();
        assertNotNull(farm.getSilos());
        assertNotEquals(0, farm.getSilos().size());
        assertEquals(silo.getUuid(), farm.getSilos().get(0));

        siloRepository.delete(silo);
        final UUID farmUUID = farm.getUuid();
        Awaitility.await()
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .atMost(500, TimeUnit.MILLISECONDS)
                .until(() -> {
                    return farmRepository.getByUuid(farmUUID).orElseThrow().getSilos().isEmpty();
                });

        farm = farmRepository.getByUuid(farm.getUuid()).orElseThrow();
        assertNotNull(farm.getSilos());
        assertEquals(0, farm.getSilos().size());
    }
}
