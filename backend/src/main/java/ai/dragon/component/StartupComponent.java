package ai.dragon.component;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ai.dragon.config.DataProperties;

@Component
public class StartupComponent implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataProperties dataProperties;

    public StartupComponent(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        createDataDirectories();
    }

    private void createDataDirectories() {
        File dataPath = new File(dataProperties.getPath());
        logger.info("Creating data directory at " + dataPath + " if doesn't exist");

        if (!dataPath.exists()) {
            boolean creationStatus = dataPath.mkdirs();

            if (creationStatus) {
                logger.debug("Data directory created successfully : " + dataPath);
            } else {
                logger.error("Failed to create data directory");
                throw new RuntimeException("Failed to create data directory : " + dataPath);
            }

        } else {
            logger.debug("Data directory already exists : " + dataPath);
        }
    }
}