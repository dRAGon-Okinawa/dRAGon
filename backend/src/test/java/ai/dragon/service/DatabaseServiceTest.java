package ai.dragon.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DatabaseServiceTest {
    @Autowired
    private DatabaseService databaseService;

    @Test
    void loadDatabase() {
        databaseService.openDatabase();
    }
}
