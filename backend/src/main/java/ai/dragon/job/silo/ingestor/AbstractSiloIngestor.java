package ai.dragon.job.silo.ingestor;

import java.util.List;

import dev.langchain4j.data.document.Document;

public interface AbstractSiloIngestor {
    public void checkIngestorSettings() throws Exception;
    public List<Document> listDocuments() throws Exception;
}
