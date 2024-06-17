package ai.dragon.job.silo.ingestor.loader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.dragon.entity.SiloEntity;
import dev.langchain4j.data.document.Document;

public class NoneIngestorLoader extends ImplAbstractSiloIngestorLoader {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public NoneIngestorLoader(SiloEntity siloEntity) throws Exception {
        super(siloEntity);
    }

    public List<Document> listIngestorDocuments() throws Exception {
        logger.info("None Ingestor Loader, no documents to ingest for Silo {}", entity.getUuid());
        return new ArrayList<>();
    }

    public void checkIngestorLoaderSettings() throws Exception {
    }
}
