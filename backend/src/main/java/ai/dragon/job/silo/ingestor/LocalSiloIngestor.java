package ai.dragon.job.silo.ingestor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.dto.IngestorDocument;
import ai.dragon.job.silo.ingestor.dto.LocalIngestorSettings;

public class LocalSiloIngestor extends ImplAbstractSiloIngestor {
    private LocalIngestorSettings localIngestorSettings;

    public LocalSiloIngestor(SiloEntity siloEntity) throws Exception {
        super(siloEntity);
    }

    public List<IngestorDocument> listDocuments() {
        return new ArrayList<IngestorDocument>();
    }

    public void checkIngestorSettings() throws Exception {
        List<String> ingestorSettings = entity.getIngestorSettings();
        if (ingestorSettings == null) {
            throw new Exception(String.format("No 'ingestorSettings' found for Silo %s", entity.getUuid()));
        }
        localIngestorSettings = convertIniSettingsToObject(ingestorSettings, LocalIngestorSettings.class);
        if (localIngestorSettings.getLocalPath() == null) {
            throw new Exception(String.format("No 'localPath' setting found for Silo's Ingestor %s", entity.getUuid()));
        }
        File ingestorPathFile = new File(localIngestorSettings.getLocalPath());
        if (!ingestorPathFile.exists() || !ingestorPathFile.isDirectory()) {
            throw new Exception(String.format("Silo's Ingestor directory not found at '%s'", ingestorPathFile));
        }
    }

}
