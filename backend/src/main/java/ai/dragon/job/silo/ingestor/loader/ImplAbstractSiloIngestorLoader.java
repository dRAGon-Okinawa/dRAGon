package ai.dragon.job.silo.ingestor.loader;

import ai.dragon.entity.SiloEntity;

public abstract class ImplAbstractSiloIngestorLoader implements AbstractSiloIngestorLoader {
    protected SiloEntity entity;

    private ImplAbstractSiloIngestorLoader() throws Exception {
    }

    public ImplAbstractSiloIngestorLoader(SiloEntity entity) throws Exception {
        this();
        this.entity = entity;
        checkIngestorLoaderSettings();
    }
}
