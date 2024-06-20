package ai.dragon.service;

import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.FarmEntity;
import ai.dragon.entity.SiloEntity;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.repository.FarmRepository;
import ai.dragon.repository.SiloRepository;
import ai.dragon.util.fluenttry.Try;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class SiloService {
    private EntityChangeListener<SiloEntity> entityChangeListener;

    @Autowired
    private SiloRepository siloRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private SiloJobService siloJobService;

    @Autowired
    private DocumentService documentService;

    @PostConstruct
    private void init() {
        entityChangeListener = siloRepository.subscribe(new EntityChangeListener<SiloEntity>() {
            @Override
            public void onChangeEvent(CollectionEventInfo<?> collectionEventInfo, SiloEntity entity) {
                switch (collectionEventInfo.getEventType()) {
                    case Remove:
                        removeFarmLinks(entity);
                        Try.create().run(() -> {
                            removeAllDocuments(entity);
                        });
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @PreDestroy
    private void destroy() {
        siloRepository.unsubscribe(entityChangeListener);
    }

    public void rebuildSilo(UUID uuid) {
        SiloEntity entity = siloRepository.getByUuid(uuid).orElseThrow();
        siloJobService.startSiloIngestorJobNow(entity);
    }

    public void removeAllDocuments(SiloEntity entity) throws Exception {
        documentService.removeDocumentsOfSilo(entity);
    }

    private void removeFarmLinks(SiloEntity entity) {
        for (FarmEntity farm : farmRepository.find()) {
            if (farm.getSilos().contains(entity.getUuid())) {
                farm.getSilos().remove(entity.getUuid());
                farmRepository.save(farm);
            }
        }
    }
}
