package ai.dragon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.dizitart.no2.repository.Cursor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.FarmEntity;

@SpringBootTest
@ActiveProfiles("test")
public class FarmRepositoryTest {
    @Autowired
    private FarmRepository farmRepository;

    @Test
    void insertFarms() {
        farmRepository.deleteAll();
        String farmName = "dRAGon Farm #1";

        FarmEntity farm = new FarmEntity();
        farm.setUuid(UUID.randomUUID());
        farm.setName(farmName);
        farmRepository.save(farm);

        FarmEntity retrievedFarm = farmRepository.getByUuid(farm.getUuid()).orElseThrow();
        assertNotNull(farmRepository.getByUuid(retrievedFarm.getUuid()));
        assertEquals(farmName, retrievedFarm.getName());
    }

    @Test
    void findAllFarms() {
        farmRepository.deleteAll();
        int nbFarmsToInsert = 3;

        for (int i = 0; i < nbFarmsToInsert; i++) {
            farmRepository.save(new FarmEntity());
        }

        assertEquals(nbFarmsToInsert, farmRepository.countAll());

        farmRepository.find().forEach(farm -> {
            assertNotNull(farm.getUuid());
        });
    }

    @Test
    void findFarmsByName() {
        farmRepository.deleteAll();
        int nbFarmsToInsert = 3;

        for (int i = 0; i < nbFarmsToInsert; i++) {
            FarmEntity farm = new FarmEntity();
            farm.setName(String.format("Farm %d", i));
            farmRepository.save(farm);
        }

        assertEquals(nbFarmsToInsert, farmRepository.countAll());

        Cursor<FarmEntity> cursor = farmRepository.findByFieldValue("name", "Farm 1");
        assertEquals(1, cursor.size());
    }

    @Test
    void deleteFarms() {
        farmRepository.deleteAll();
        int nbFarmsToInsert = 3;

        for (int i = 0; i < nbFarmsToInsert; i++) {
            farmRepository.save(new FarmEntity());
        }

        assertEquals(nbFarmsToInsert, farmRepository.countAll());

        farmRepository.find().forEach(farm -> {
            farmRepository.delete(farm.getUuid());
        });

        assertEquals(0, farmRepository.countAll());
    }
}
