package ai.dragon.job.silo.ingestor;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.dto.LocalIngestorSettings;
import ai.dragon.util.IniSettingUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;

public class LocalSiloIngestor extends ImplAbstractSiloIngestor {
    private LocalIngestorSettings localIngestorSettings;
    private File ingestorPathFile;

    public LocalSiloIngestor(SiloEntity siloEntity) throws Exception {
        super(siloEntity);
    }

    public List<Document> listDocuments() throws Exception {
        PathMatcher pathMatcher = localIngestorSettings.getPathMatcher() != null
                ? FileSystems.getDefault().getPathMatcher(localIngestorSettings.getPathMatcher())
                : FileSystems.getDefault().getPathMatcher("glob:**");
        return localIngestorSettings.isRecursive()
                ? FileSystemDocumentLoader.loadDocumentsRecursively(ingestorPathFile.toPath(), pathMatcher)
                : FileSystemDocumentLoader.loadDocuments(ingestorPathFile.toPath(), pathMatcher);
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
}
