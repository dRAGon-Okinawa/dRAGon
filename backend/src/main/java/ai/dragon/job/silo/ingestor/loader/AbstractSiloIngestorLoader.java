package ai.dragon.job.silo.ingestor.loader;

import java.util.List;

import dev.langchain4j.data.document.Document;

public interface AbstractSiloIngestorLoader {
    public void checkIngestorLoaderSettings() throws Exception;
    public List<Document> listDocuments() throws Exception;
}
