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
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;

public class FileSystemIngestorLoader extends ImplAbstractSiloIngestorLoader {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private FileSystemIngestorLoaderSettings loaderSettings = new FileSystemIngestorLoaderSettings();
    private List<File> pathsToIngest = new ArrayList<>();

    public FileSystemIngestorLoader(SiloEntity siloEntity, FileSystemIngestorLoaderSettings settings) throws Exception {
        super(siloEntity);
        loaderSettings = settings;
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
                        loaderSettings.isRecursive(), pathToIngest);
                PathMatcher pathMatcher = loaderSettings.getPathMatcher() != null
                        ? FileSystems.getDefault().getPathMatcher(loaderSettings.getPathMatcher())
                        : FileSystems.getDefault()
                                .getPathMatcher(FileSystemIngestorLoaderSettings.DEFAULT_PATH_MATCHER);
                List<Document> documents = loaderSettings.isRecursive()
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
        List<String> ingestorLoaderSettings = entity.getIngestorSettings();
        if (ingestorLoaderSettings == null) {
            logger.warn("No 'ingestorLoaderSettings' provided for Silo {}", entity.getUuid());
            return;
        }
        if (loaderSettings.getPaths() == null) {
            logger.warn("No 'paths' setting found for Silo's Ingestor {}", entity.getUuid());
            return;
        }
        List<String> paths = loaderSettings.getPaths();
        pathsToIngest.clear();
        for (String path : paths) {
            File pathFile = new File(path.trim());
            if (!pathFile.exists() || !pathFile.isDirectory()) {
                logger.warn("Skipping directory because not found : {}", pathFile);
                continue;
            }
            pathsToIngest.add(pathFile);
        }
    }
}
