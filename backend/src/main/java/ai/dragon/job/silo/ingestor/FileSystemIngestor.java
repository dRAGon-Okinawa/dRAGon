package ai.dragon.job.silo.ingestor;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.dto.FileSystemIngestorSettings;
import ai.dragon.util.IniSettingUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;

public class FileSystemIngestor extends ImplAbstractSiloIngestor {
    private FileSystemIngestorSettings fileSystemIngestorSettings;
    private File ingestorPathFile;

    public FileSystemIngestor(SiloEntity siloEntity) throws Exception {
        super(siloEntity);
    }

    public List<Document> listDocuments() throws Exception {
        PathMatcher pathMatcher = fileSystemIngestorSettings.getPathMatcher() != null
                ? FileSystems.getDefault().getPathMatcher(fileSystemIngestorSettings.getPathMatcher())
                : FileSystems.getDefault().getPathMatcher(FileSystemIngestorSettings.DEFAULT_PATH_MATCHER);
        List<Document> documents = fileSystemIngestorSettings.isRecursive()
                ? FileSystemDocumentLoader.loadDocumentsRecursively(ingestorPathFile.toPath(), pathMatcher,
                        new ApacheTikaDocumentParser())
                : FileSystemDocumentLoader.loadDocuments(ingestorPathFile.toPath(), pathMatcher,
                        new ApacheTikaDocumentParser());
        for (Document document : documents) {
            File file = new File(document.metadata().getString("absolute_directory_path"),
                    document.metadata().getString("file_name"));
            document.metadata().put("silo_uuid", entity.getUuid().toString());
            document.metadata().put("file_date", file.lastModified());
            document.metadata().put("file_size", file.length());
        }
        return documents;
    }

    public void checkIngestorSettings() throws Exception {
        List<String> ingestorSettings = entity.getIngestorSettings();
        if (ingestorSettings == null) {
            throw new Exception(String.format("No 'ingestorSettings' found for Silo %s", entity.getUuid()));
        }
        fileSystemIngestorSettings = IniSettingUtil.convertIniSettingsToObject(ingestorSettings,
                FileSystemIngestorSettings.class);
        if (fileSystemIngestorSettings.getPath() == null) {
            throw new Exception(String.format("No 'path' setting found for Silo's Ingestor %s", entity.getUuid()));
        }
        ingestorPathFile = new File(fileSystemIngestorSettings.getPath());
        if (!ingestorPathFile.exists() || !ingestorPathFile.isDirectory()) {
            throw new Exception(String.format("Silo's Ingestor directory not found at '%s'", ingestorPathFile));
        }
    }
}
