package ai.dragon.component;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ai.dragon.properties.DataProperties;
import lombok.Getter;

@Component
public class DirectoryStructureComponent implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataProperties dataProperties;

    @Getter
    private File dataDirectory;

    public DirectoryStructureComponent(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        dataDirectory = createDataDirectories();
        createDatabaseDirectory();
    }

    private File createDataDirectories() throws Exception {
        String path = Optional.ofNullable(dataProperties.getPath()).orElse(":temp:");
        File dataDirectory = ":temp:".equals(path) ? Files.createTempDirectory("dRAGon").toFile() : new File(path);
        logger.info("Creating data directory at " + dataDirectory + " if doesn't exist");

        if (!dataDirectory.exists()) {
            boolean creationStatus = dataDirectory.mkdirs();

            if (creationStatus) {
                logger.debug("Data directory created successfully : " + dataDirectory);
            } else {
                logger.error("Failed to create data directory");
                throw new RuntimeException("Failed to create data directory : " + dataDirectory);
            }

        } else {
            logger.debug("Data directory already exists : " + dataDirectory);
        }

        return dataDirectory;
    }

    private void createDatabaseDirectory() {
        logger.debug("Creating database directory if doesn't exist");
        File databaseDirectory = new File(dataDirectory, "db");

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
