package ai.dragon.controller.api.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.dto.api.GenericApiResponse;
import ai.dragon.enumeration.ApiResponseCode;
import ai.dragon.repository.FarmRepository;
import ai.dragon.util.UUIDUtil;

@SpringBootTest
@ActiveProfiles("test")
public class AbstractCrudBackendApiControllerTest {
    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private FarmBackendApiController farmBackendApiController;

    @Test
    public void transactionalCreate() throws Exception {
        Map<String, Object> farmFields = new HashMap<>();
        farmFields.put("name", "Test Farm");
        farmFields.put("raagIdentifier", "test-farm");

        farmRepository.deleteAll();
        GenericApiResponse apiResponse = farmBackendApiController.upsertFarm(UUIDUtil.zeroUUIDString(), farmFields);
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getData());
        assertEquals(ApiResponseCode.SUCCESS.toString(), apiResponse.getCode());
        assertEquals(1, farmRepository.countAll());
        
        GenericApiResponse apiResponseDuplicates = farmBackendApiController.upsertFarm(UUIDUtil.zeroUUIDString(), farmFields);
        assertNotNull(apiResponseDuplicates);
        assertEquals(ApiResponseCode.DUPLICATES.toString(), apiResponseDuplicates.getCode());
    }
}
