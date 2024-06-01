package ai.dragon.component;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ai.dragon.properties.config.DataProperties;
import jakarta.annotation.PostConstruct;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DirectoryStructureComponent {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataProperties dataProperties;

    private File mainDataDirectory;

    @PostConstruct
    private void postConstruct() throws Exception {
        mainDataDirectory = createMainDataDirectory();
    }

    public File directoryFor(String directoryName) {
        if (mainDataDirectory == null || !mainDataDirectory.exists()) {
            throw new RuntimeException("Data directory not found");
        }
        File directory = new File(mainDataDirectory, directoryName);
        if (!directory.exists()) {
            boolean creationStatus = directory.mkdirs();
            if (creationStatus) {
                logger.debug("Directory created successfully : " + directory);
            } else {
                logger.error("Failed to create directory");
                throw new RuntimeException("Failed to create directory : " + directory);
            }
        } else {
            logger.debug("Directory already exists : " + directory);
        }
        return directory;
    }

    private File createMainDataDirectory() throws Exception {
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
}
