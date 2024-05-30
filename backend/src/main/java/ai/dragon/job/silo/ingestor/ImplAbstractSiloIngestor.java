package ai.dragon.job.silo.ingestor;

import ai.dragon.entity.SiloEntity;

public abstract class ImplAbstractSiloIngestor implements AbstractSiloIngestor {
    protected SiloEntity entity;

    private ImplAbstractSiloIngestor() throws Exception {
    }

    public ImplAbstractSiloIngestor(SiloEntity entity) throws Exception {
        this();
        this.entity = entity;
        checkIngestorSettings();
    }
}
