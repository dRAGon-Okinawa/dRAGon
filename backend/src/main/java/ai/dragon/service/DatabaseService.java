package ai.dragon.service;

import java.io.File;
import java.io.OutputStream;
import java.util.Optional;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.common.mapper.JacksonMapperModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.support.exchange.ExportOptions;
import org.dizitart.no2.support.exchange.Exporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.component.DirectoryStructureComponent;
import ai.dragon.properties.config.DataProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class DatabaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Nitrite db;

    @Autowired
    private DataProperties dataProperties;

    @Autowired
    private DirectoryStructureComponent directoryStructureComponent;

    public Nitrite getNitriteDB() {
        if (db == null || db.isClosed()) {
            openDatabase();
        }

        return db;
    }

    @PreDestroy
    public void closeDatabase() {
        if (db != null && !db.isClosed()) {
            db.close();
        }
    }

    @PostConstruct
    public void openDatabase() {
        if (db != null && !db.isClosed()) {
            logger.debug("Database is already opened");
            return;
        }

        NitriteBuilder dbBuilder = Nitrite
                .builder()
                .loadModule(new JacksonMapperModule());

        if (!isDatabaseInMemory()) {
            File databaseFile = new File(directoryStructureComponent.directoryFor("db"), getDatabaseFilename());
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

    public void exportDatabase(String fileOutput) {
        ExportOptions exportOptions = new ExportOptions();
        exportOptions.setNitriteFactory(() -> getNitriteDB());

        Exporter exporter = Exporter.withOptions(exportOptions);
        exporter.exportTo(fileOutput);
    }

    public void exportDatabase(OutputStream outputStream) throws Exception {
        ExportOptions exportOptions = new ExportOptions();
        exportOptions.setNitriteFactory(() -> getNitriteDB());

        Exporter exporter = Exporter.withOptions(exportOptions);
        exporter.exportTo(outputStream);
    }

    private String getDatabaseFilename() {
        return Optional.ofNullable(dataProperties.getDb()).orElse("dragon.db");
    }

    private boolean isDatabaseInMemory() {
        return ":memory:".equals(getDatabaseFilename());
    }
}
