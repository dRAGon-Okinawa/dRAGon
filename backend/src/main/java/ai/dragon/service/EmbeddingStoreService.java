package ai.dragon.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.component.DirectoryStructureComponent;
import ai.dragon.entity.SiloEntity;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.properties.store.PersistInMemoryEmbeddingStoreSettings;
import ai.dragon.repository.SiloRepository;
import ai.dragon.util.embedding.store.inmemory.persist.PersistInMemoryEmbeddingStore;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class EmbeddingStoreService {
    private Map<UUID, EmbeddingStore<TextSegment>> embeddingStores = new HashMap<>();

    @Autowired
    private SiloRepository siloRepository;

    @Autowired
    private EmbeddingModelService embeddingModelService;

    @Autowired
    private DirectoryStructureComponent directoryStructureComponent;

    @Autowired
    private KVSettingService kvSettingService;

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

    public EmbeddingStore<TextSegment> retrieveEmbeddingStore(UUID siloUuid) throws Exception {
        if (embeddingStores.containsKey(siloUuid)) {
            return embeddingStores.get(siloUuid);
        }
        SiloEntity siloEntity = siloRepository.getByUuid(siloUuid).orElseThrow();
        EmbeddingStore<TextSegment> embeddingStore = buildEmbeddingStore(siloEntity);
        embeddingStores.put(siloUuid, embeddingStore);
        return embeddingStore;
    }

    public void closeEmbeddingStore(UUID siloUuid) {
        EmbeddingStore<TextSegment> embeddingStore = embeddingStores.get(siloUuid);
        if (embeddingStore != null) {
            embeddingStores.remove(siloUuid);
        }
    }

    public void closeAllEmbeddingStores() {
        for (UUID siloUuid : embeddingStores.keySet()) {
            closeEmbeddingStore(siloUuid);
        }
    }

    public void clearEmbeddingStore(UUID siloUuid) throws Exception {
        EmbeddingStore<TextSegment> embeddingStore = retrieveEmbeddingStore(siloUuid);
        embeddingStore.removeAll();
    }

    public EmbeddingSearchResult<TextSegment> query(UUID siloUuid, String query, Integer maxResults) throws Exception {
        SiloEntity siloEntity = siloRepository.getByUuid(siloUuid).orElseThrow();
        EmbeddingStore<TextSegment> embeddingStore = retrieveEmbeddingStore(siloUuid);
        EmbeddingModel embeddingModel = embeddingModelService.modelForSilo(siloEntity);
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        // Filter onlyForUser1 = metadataKey("userId").isEqualTo("1");
        EmbeddingSearchRequest embeddingSearchRequest1 = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                // .filter(onlyForUser1)
                .maxResults(maxResults)
                .build();
        return embeddingStore.search(embeddingSearchRequest1);
    }

    private EmbeddingStore<TextSegment> buildEmbeddingStore(SiloEntity siloEntity) throws Exception {
        switch (siloEntity.getVectorStore()) {
            case InMemoryEmbeddingStore:
                return PersistInMemoryEmbeddingStore.builder().build();
            case PersistInMemoryEmbeddingStore:
                PersistInMemoryEmbeddingStoreSettings storeSettings = kvSettingService.kvSettingsToObject(
                        siloEntity.getVectorStoreSettings(), PersistInMemoryEmbeddingStoreSettings.class);
                File vectorFile = new File(directoryStructureComponent.directoryFor("vector"),
                        siloEntity.getUuid().toString() + ".json");
                return PersistInMemoryEmbeddingStore
                        .builder()
                        .flushSecs(storeSettings.getFlushSecs())
                        .persistFile(vectorFile)
                        .build();
            default:
                throw new UnsupportedOperationException(
                        String.format("VectorStoreType not supported : %s", siloEntity.getVectorStore()));
        }
    }
}
