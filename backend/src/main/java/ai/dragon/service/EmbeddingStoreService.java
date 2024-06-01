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
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class EmbeddingStoreService {
    private Map<UUID, EmbeddingStore<TextSegment>> embeddingStores = new HashMap<>();

    @Autowired
    private SiloRepository siloRepository;

    @Autowired
    private EmbeddingModelService embeddingModelService;

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

    public void query(UUID siloUuid, String query) throws Exception {
        SiloEntity siloEntity = siloRepository.getByUuid(siloUuid).orElseThrow();
        EmbeddingStore<TextSegment> embeddingStore = retrieveEmbeddingStore(siloUuid);
        EmbeddingModel embeddingModel = embeddingModelService.modelForEntity(siloEntity);
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        // Filter onlyForUser1 = metadataKey("userId").isEqualTo("1");
        EmbeddingSearchRequest embeddingSearchRequest1 = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                // .filter(onlyForUser1)
                .build();
        EmbeddingSearchResult<TextSegment> embeddingSearchResult1 = embeddingStore.search(embeddingSearchRequest1);
        for (EmbeddingMatch<TextSegment> embeddingMatch : embeddingSearchResult1.matches()) {
            System.out.println("=> " + embeddingMatch.score() + " : " +
                    embeddingMatch.embedded().metadata());
            System.out.println(embeddingMatch.embedded().text());
            System.out.println("=====");
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
