package ai.dragon.job.silo.ingestor;

import java.util.List;

import ai.dragon.job.silo.ingestor.dto.IngestorDocument;

public interface AbstractSiloIngestor {
    public void checkIngestorSettings() throws Exception;
    public List<IngestorDocument> listDocuments();
}
