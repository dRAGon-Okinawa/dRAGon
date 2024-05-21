package ai.dragon.service;

import java.io.File;

import org.dizitart.no2.Nitrite;
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
        if(db != null && !db.isClosed()) {
            logger.debug("Database is already opened");
            return;
        }

        // Create database directory if doesn't exist
        createDatabaseDirectory();

        File databaseFile = new File(dataProperties.getPath(), "db/dragon.db");
        logger.debug("Opening database : " + databaseFile);

        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath(databaseFile)
                .build();

        db = Nitrite.builder()
                .loadModule(storeModule)
                .openOrCreate();
    }

    private void createDatabaseDirectory() {
        logger.debug("Creating database directory if doesn't exist");
        File databaseDirectory = new File(dataProperties.getPath(), "db");

        if (!databaseDirectory.exists()) {
            boolean creationStatus = databaseDirectory.mkdirs();

            if (creationStatus) {
                logger.debug("Database directory created successfully : " + databaseDirectory);
            } else {
                logger.error("Failed to create database directory");
                throw new RuntimeException("Failed to create database directory : " + databaseDirectory);
            }

        } else {
            logger.debug("Database directory already exists : " + databaseDirectory);
        }
    }
}
