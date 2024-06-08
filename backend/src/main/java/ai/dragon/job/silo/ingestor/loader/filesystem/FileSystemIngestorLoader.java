package ai.dragon.job.silo.ingestor.loader.filesystem;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.loader.ImplAbstractSiloIngestorLoader;
import ai.dragon.properties.loader.FileSystemIngestorLoaderSettings;
import ai.dragon.util.IniSettingUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;

public class FileSystemIngestorLoader extends ImplAbstractSiloIngestorLoader {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private FileSystemIngestorLoaderSettings fileSystemIngestorLoaderSettings = new FileSystemIngestorLoaderSettings();
    private List<File> pathsToIngest = new ArrayList<>();

    public FileSystemIngestorLoader(SiloEntity siloEntity) throws Exception {
        super(siloEntity);
    }

    public List<Document> listIngestorDocuments() throws Exception {
        List<Document> allDocuments = new ArrayList<>();
        if (pathsToIngest == null || pathsToIngest.isEmpty()) {
            logger.warn("No paths to ingest for Silo {}", entity.getUuid());
            return allDocuments;
        }
        for (File pathToIngest : pathsToIngest) {
            try {
                logger.info("Listing documents (recursive: {}) for path : {}...",
                        fileSystemIngestorLoaderSettings.isRecursive(), pathToIngest);
                PathMatcher pathMatcher = fileSystemIngestorLoaderSettings.getPathMatcher() != null
                        ? FileSystems.getDefault().getPathMatcher(fileSystemIngestorLoaderSettings.getPathMatcher())
                        : FileSystems.getDefault()
                                .getPathMatcher(FileSystemIngestorLoaderSettings.DEFAULT_PATH_MATCHER);
                List<Document> documents = fileSystemIngestorLoaderSettings.isRecursive()
                        ? FileSystemDocumentLoader.loadDocumentsRecursively(pathToIngest.toPath(),
                                pathMatcher,
                                new ApacheTikaDocumentParser())
                        : FileSystemDocumentLoader.loadDocuments(pathToIngest.toPath(), pathMatcher,
                                new ApacheTikaDocumentParser());
                for (Document document : documents) {
                    File file = new File(document.metadata().getString("absolute_directory_path"),
                            document.metadata().getString("file_name"));
                    document.metadata().put("silo_uuid", entity.getUuid().toString());
                    document.metadata().put("document_date", file.lastModified());
                    document.metadata().put("document_size", file.length());
                    document.metadata().put("document_location", file.getAbsolutePath());
                }
                logger.info("Found {} documents for path : {}", documents.size(), pathToIngest);
                allDocuments.addAll(documents);
            } catch (Exception e) {
                logger.error("Error listing documents for path '{}' : {}", pathToIngest, e);
            }
        }
        return allDocuments;
    }

    public void checkIngestorLoaderSettings() throws Exception {
        List<String> ingestorLoaderSettings = entity.getIngestorLoaderSettings();
        if (ingestorLoaderSettings == null) {
            logger.warn("No 'ingestorLoaderSettings' provided for Silo {}", entity.getUuid());
            return;
        }
        fileSystemIngestorLoaderSettings = IniSettingUtil.convertIniSettingsToObject(ingestorLoaderSettings,
                FileSystemIngestorLoaderSettings.class);
        if (fileSystemIngestorLoaderSettings.getPath() == null) {
            logger.warn("No 'path' setting found for Silo's Ingestor {}", entity.getUuid());
            return;
        }
        String[] paths = fileSystemIngestorLoaderSettings.getPath().trim().split(",");
        pathsToIngest.clear();
        for (String path : paths) {
            File pathFile = new File(path);
            if (!pathFile.exists() || !pathFile.isDirectory()) {
                logger.warn("Skipping directory because not found : {}", pathFile);
                continue;
            }
            pathsToIngest.add(pathFile);
        }
    }
}
