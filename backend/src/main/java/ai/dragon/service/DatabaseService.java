package ai.dragon.service;

import java.io.File;
import java.util.Optional;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.config.DataProperties;

@Service
public class DatabaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Nitrite db;

    @Autowired
    private DataProperties dataProperties;

    public void openDatabase() {
        if (db != null && !db.isClosed()) {
            logger.debug("Database is already opened");
            return;
        }

        NitriteBuilder dbBuilder = Nitrite.builder();

        if (!dataProperties.getDb().equals(":memory:")) {
            String dbName = Optional.ofNullable(dataProperties.getDb()).orElse("dragon.db");
            File databaseFile = new File(dataProperties.getPath(), "db/" + dbName);
            logger.debug("Will use database file: " + databaseFile);

            MVStoreModule storeModule = MVStoreModule.withConfig()
                    .filePath(databaseFile)
                    .build();

            dbBuilder.loadModule(storeModule);

        } else {
            logger.debug("Will use in-memory database");
        }

        db = dbBuilder.openOrCreate();
    }
}
