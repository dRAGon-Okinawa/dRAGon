package ai.dragon.job.silo.ingestor.loader;

import java.util.List;

import ai.dragon.entity.SiloEntity;
import dev.langchain4j.data.document.Document;

public abstract class ImplAbstractSiloIngestorLoader implements AbstractSiloIngestorLoader {
    protected SiloEntity entity;

    private ImplAbstractSiloIngestorLoader() throws Exception {
    }

    public ImplAbstractSiloIngestorLoader(SiloEntity entity) throws Exception {
        this();
        this.entity = entity;
    }

    public List<Document> listDocuments() throws Exception {
        checkIngestorLoaderSettings();
        return listIngestorDocuments();
    }

    protected abstract List<Document> listIngestorDocuments() throws Exception;
}
