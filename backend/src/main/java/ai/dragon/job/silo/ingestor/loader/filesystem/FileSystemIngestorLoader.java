package ai.dragon.job.silo.ingestor.loader.filesystem;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.loader.ImplAbstractSiloIngestorLoader;
import ai.dragon.util.IniSettingUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;

public class FileSystemIngestorLoader extends ImplAbstractSiloIngestorLoader {
    private FileSystemIngestorLoaderSettings fileSystemIngestorLoaderSettings;
    private File ingestorLoaderPathFile;

    public FileSystemIngestorLoader(SiloEntity siloEntity) throws Exception {
        super(siloEntity);
    }

    public List<Document> listDocuments() throws Exception {
        PathMatcher pathMatcher = fileSystemIngestorLoaderSettings.getPathMatcher() != null
                ? FileSystems.getDefault().getPathMatcher(fileSystemIngestorLoaderSettings.getPathMatcher())
                : FileSystems.getDefault().getPathMatcher(FileSystemIngestorLoaderSettings.DEFAULT_PATH_MATCHER);
        List<Document> documents = fileSystemIngestorLoaderSettings.isRecursive()
                ? FileSystemDocumentLoader.loadDocumentsRecursively(ingestorLoaderPathFile.toPath(), pathMatcher,
                        new ApacheTikaDocumentParser())
                : FileSystemDocumentLoader.loadDocuments(ingestorLoaderPathFile.toPath(), pathMatcher,
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

    public void checkIngestorLoaderSettings() throws Exception {
        List<String> ingestorLoaderSettings = entity.getIngestorLoaderSettings();
        if (ingestorLoaderSettings == null) {
            throw new Exception(String.format("No 'ingestorLoaderSettings' found for Silo %s", entity.getUuid()));
        }
        fileSystemIngestorLoaderSettings = IniSettingUtil.convertIniSettingsToObject(ingestorLoaderSettings,
                FileSystemIngestorLoaderSettings.class);
        if (fileSystemIngestorLoaderSettings.getPath() == null) {
            throw new Exception(String.format("No 'path' setting found for Silo's Ingestor %s", entity.getUuid()));
        }
        ingestorLoaderPathFile = new File(fileSystemIngestorLoaderSettings.getPath());
        if (!ingestorLoaderPathFile.exists() || !ingestorLoaderPathFile.isDirectory()) {
            throw new Exception(
                    String.format("Silo's Ingestor Loader directory not found at '%s'", ingestorLoaderPathFile));
        }
    }
}
