package ai.dragon.job.silo.ingestor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.dto.IngestorDocument;
import ai.dragon.job.silo.ingestor.dto.LocalIngestorSettings;
import ai.dragon.util.IniSettingUtil;

public class LocalSiloIngestor extends ImplAbstractSiloIngestor {
    private LocalIngestorSettings localIngestorSettings;
    private File ingestorPathFile;

    public LocalSiloIngestor(SiloEntity siloEntity) throws Exception {
        super(siloEntity);
    }

    public List<IngestorDocument> listDocuments() throws Exception {
        List<IngestorDocument> documentsFound = new ArrayList<>();

        makeDocumentsList(ingestorPathFile.toPath(), documentsFound);

        return documentsFound;
    }

    public void checkIngestorSettings() throws Exception {
        List<String> ingestorSettings = entity.getIngestorSettings();
        if (ingestorSettings == null) {
            throw new Exception(String.format("No 'ingestorSettings' found for Silo %s", entity.getUuid()));
        }
        localIngestorSettings = IniSettingUtil.convertIniSettingsToObject(ingestorSettings,
                LocalIngestorSettings.class);
        if (localIngestorSettings.getLocalPath() == null) {
            throw new Exception(String.format("No 'localPath' setting found for Silo's Ingestor %s", entity.getUuid()));
        }
        ingestorPathFile = new File(localIngestorSettings.getLocalPath());
        if (!ingestorPathFile.exists() || !ingestorPathFile.isDirectory()) {
            throw new Exception(String.format("Silo's Ingestor directory not found at '%s'", ingestorPathFile));
        }
    }

    private void makeDocumentsList(Path currentPath, List<IngestorDocument> allFiles) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && localIngestorSettings.isRecursive()) {
                    makeDocumentsList(entry, allFiles);
                } else if (Files.isRegularFile(entry)) {
                    URI uri = entry.toUri();
                    if (localIngestorSettings.getMatches() != null
                            && !uri.toString().matches(localIngestorSettings.getMatches())) {
                        continue;
                    }
                    IngestorDocument document = new IngestorDocument();
                    document.setUri(uri);
                    allFiles.add(document);
                }
            }
        }
    }
}
