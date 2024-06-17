package ai.dragon.job.silo.ingestor.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.dragon.entity.SiloEntity;
import ai.dragon.properties.loader.URLIngestorLoaderSettings;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;

public class URLIngestorLoader extends ImplAbstractSiloIngestorLoader {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private URLIngestorLoaderSettings loaderSettings = new URLIngestorLoaderSettings();
    private List<URL> urlsToIngest = new ArrayList<>();

    public URLIngestorLoader(SiloEntity siloEntity, URLIngestorLoaderSettings settings) throws Exception {
        super(siloEntity);
        loaderSettings = settings;
    }

    public List<Document> listIngestorDocuments() throws Exception {
        List<Document> allDocuments = new ArrayList<>();
        if (urlsToIngest == null || urlsToIngest.isEmpty()) {
            logger.warn("No URLs to ingest for Silo {}", entity.getUuid());
            return allDocuments;
        }
        for (URL urlToIngest : urlsToIngest) {
            try {
                logger.info("Retrieving document for URL : {}...", urlToIngest);
                Document document = UrlDocumentLoader.load(urlToIngest, new ApacheTikaDocumentParser());
                // TODO allow automatically extract metadata (e.g. creator, last-author,
                // created/modified timestamp, etc
                // TODO langchain4j =>
                // src/main/java/dev/langchain4j/data/document/parser/apache/tika/ApacheTikaDocumentParser.java#L90
                document.metadata().put("file_name", urlToIngest.getFile());
                document.metadata().put("silo_uuid", entity.getUuid().toString());
                document.metadata().put("document_date", System.currentTimeMillis()); // TODO
                document.metadata().put("document_size", 0); // TODO
                document.metadata().put("document_location", urlToIngest.toString());
                allDocuments.add(document);
            } catch (Exception e) {
                logger.error("Error retrieveing document for URL '{}' : {}", urlToIngest, e);
            }
        }
        return allDocuments;
    }

    public void checkIngestorLoaderSettings() throws Exception {
        List<String> ingestorLoaderSettings = entity.getIngestorSettings();
        if (ingestorLoaderSettings == null) {
            logger.warn("No 'ingestorSettings' provided for Silo {}", entity.getUuid());
            return;
        }
        if (loaderSettings.getUrls() == null) {
            logger.warn("No 'urls' setting found for Silo's Ingestor {}", entity.getUuid());
            return;
        }
        List<String> urls = loaderSettings.getUrls();
        urlsToIngest.clear();
        for (String url : urls) {
            try {
                URL urlToIngest = new URL(url);
                urlToIngest.toURI();
                urlsToIngest.add(urlToIngest);
            } catch (Exception ex) {
                logger.error("Error parsing URL '{}'", url, ex);
            }
        }
    }
}
