package ai.dragon.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.repository.SiloRepository;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class EmbeddingStoreService {
    private Map<UUID, EmbeddingStore<TextSegment>> embeddingStores = new HashMap<>();

    @Autowired
    private SiloRepository siloRepository;

    private EntityChangeListener<SiloEntity> entityChangeListener;

    @PostConstruct
    private void init() {
        // Subscribe to SiloEntity changes
        entityChangeListener = siloRepository.subscribe(new EntityChangeListener<SiloEntity>() {
            @Override
            public void onChangeEvent(CollectionEventInfo<?> collectionEventInfo, SiloEntity entity) {
                switch (collectionEventInfo.getEventType()) {
                    case Remove, Update:
                        closeEmbeddingStore(entity.getUuid());
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
        closeAllEmbeddingStores();
    }

    public EmbeddingStore<TextSegment> retrieveEmbeddingStore(UUID siloUuid) {
        if (embeddingStores.containsKey(siloUuid)) {
            return embeddingStores.get(siloUuid);
        }
        SiloEntity siloEntity = siloRepository.getByUuid(siloUuid).orElseThrow();
        EmbeddingStore<TextSegment> embeddingStore = buildEmbeddingStore(siloEntity);
        embeddingStores.put(siloUuid, embeddingStore);
        return embeddingStore;
    }

    public void closeEmbeddingStore(UUID siloUuid) {
        if (embeddingStores.containsKey(siloUuid)) {
            embeddingStores.remove(siloUuid);
        }
    }

    public void closeAllEmbeddingStores() {
        for (UUID siloUuid : embeddingStores.keySet()) {
            closeEmbeddingStore(siloUuid);
        }
    }

    public void clearEmbeddingStore(UUID siloUuid) {
        if (embeddingStores.containsKey(siloUuid)) {
            embeddingStores.get(siloUuid).removeAll();
        }
    }

    private EmbeddingStore<TextSegment> buildEmbeddingStore(SiloEntity siloEntity) {
        EmbeddingStore<TextSegment> embeddingStore = null;

        switch (siloEntity.getVectorStoreType()) {
            case InMemoryEmbeddingStore:
                embeddingStore = new InMemoryEmbeddingStore<>();
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("VectorStoreType not supported : %s", siloEntity.getVectorStoreType()));
        }

        return embeddingStore;
    }
}
