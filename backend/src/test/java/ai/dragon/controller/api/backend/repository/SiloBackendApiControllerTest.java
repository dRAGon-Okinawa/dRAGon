package ai.dragon.controller.api.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;
import ai.dragon.repository.util.Pager;

@SpringBootTest
@ActiveProfiles("test")
public class SiloBackendApiControllerTest {
    @AfterAll
    static void beforeAll(@Autowired SiloRepository siloRepository) throws Exception {
        siloRepository.deleteAll();
    }

    @BeforeEach
    void beforeEach(@Autowired SiloRepository siloRepository) throws Exception {
        siloRepository.deleteAll();
    }

    @Test
    public void testPager(@Autowired SiloBackendApiController siloBackendApiController,
            @Autowired SiloRepository siloRepository) throws Exception {
        for (int i = 0; i < 105; i++) {
            siloBackendApiController.createSilo();
        }

        Pager<SiloEntity> pager = siloBackendApiController.page(siloRepository, 1, 10);
        assertNotNull(pager);
        assertEquals(10, pager.getSize());
        assertEquals(1, pager.getPage());
        assertEquals(105, pager.getTotal());
        assertNotNull(pager.getData());
        assertEquals(10, pager.getData().size());

        pager = siloBackendApiController.page(siloRepository, 11, 10);
        assertNotNull(pager);
        assertEquals(10, pager.getSize());
        assertEquals(11, pager.getPage());
        assertEquals(105, pager.getTotal());
        assertNotNull(pager.getData());
        assertEquals(5, pager.getData().size());
    }
}
