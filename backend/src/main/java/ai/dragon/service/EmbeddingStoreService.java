package ai.dragon.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.component.DirectoryStructureComponent;
import ai.dragon.entity.DocumentEntity;
import ai.dragon.entity.FarmEntity;
import ai.dragon.entity.SiloEntity;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.properties.store.PGVectorEmbeddingStoreSettings;
import ai.dragon.properties.store.PersistInMemoryEmbeddingStoreSettings;
import ai.dragon.repository.SiloRepository;
import ai.dragon.util.KVSettingUtil;
import ai.dragon.util.embedding.store.inmemory.persist.PersistInMemoryEmbeddingStore;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
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

    public void closeAllEmbeddingStores() {
        for (UUID siloUuid : embeddingStores.keySet()) {
            closeEmbeddingStore(siloUuid);
        }
    }

    public void clearEmbeddingStore(SiloEntity silo) throws Exception {
        EmbeddingStore<TextSegment> embeddingStore = retrieveEmbeddingStore(silo.getUuid());
        embeddingStore.removeAll();
    }

    public void removeEmbeddingsForDocument(DocumentEntity documentEntity) throws Exception {
        EmbeddingStore<TextSegment> embeddingStore = retrieveEmbeddingStore(documentEntity.getSiloIdentifier());
        Filter documentFilter = MetadataFilterBuilder.metadataKey("document_location")
                .isEqualTo(documentEntity.getLocation());
        embeddingStore.removeAll(documentFilter);
    }

    public List<EmbeddingMatch<TextSegment>> query(SiloEntity silo, String query, int maxResults, double minScore)
            throws Exception {
        return query(List.of(silo), query, maxResults, minScore);
    }

    public List<EmbeddingMatch<TextSegment>> query(FarmEntity farm, String query, int maxResults, double minScore)
            throws Exception {
        List<SiloEntity> silos = new ArrayList<>();
        for (UUID siloUuid : farm.getSilos()) {
            SiloEntity silo = siloRepository.getByUuid(siloUuid).orElseThrow();
            silos.add(silo);
        }
        return query(silos, query, maxResults, minScore);
    }

    public List<EmbeddingMatch<TextSegment>> query(List<SiloEntity> silos, String query, int maxResults, double minScore)
            throws Exception {
        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();
        for (SiloEntity silo : silos) {
            EmbeddingStore<TextSegment> embeddingStore = retrieveEmbeddingStore(silo.getUuid());
            EmbeddingModel embeddingModel = embeddingModelService.modelForSilo(silo);
            Embedding queryEmbedding = embeddingModel.embed(query).content();
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(maxResults)
                    .minScore(minScore)
                    .build();
            EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(embeddingSearchRequest);
            matches.addAll(searchResult.matches());
        }
        matches.sort((m1, m2) -> Double.compare(m2.score(), m1.score()));
        return matches.subList(0, Math.min(maxResults, matches.size()));
    }

    private void closeEmbeddingStore(UUID siloUuid) {
        EmbeddingStore<TextSegment> embeddingStore = embeddingStores.get(siloUuid);
        if (embeddingStore != null) {
            embeddingStores.remove(siloUuid);
        }
    }

    private EmbeddingStore<TextSegment> buildEmbeddingStore(SiloEntity siloEntity) throws Exception {
        switch (siloEntity.getVectorStore()) {
            case InMemoryEmbeddingStore:
                return PersistInMemoryEmbeddingStore.builder().build();
            case PersistInMemoryEmbeddingStore:
                PersistInMemoryEmbeddingStoreSettings persistInMemoryEmbeddingStoreSettings = KVSettingUtil
                        .kvSettingsToObject(
                                siloEntity.getVectorStoreSettings(), PersistInMemoryEmbeddingStoreSettings.class);
                File vectorFile = new File(directoryStructureComponent.directoryFor("vector"),
                        siloEntity.getUuid().toString() + ".json");
                return PersistInMemoryEmbeddingStore
                        .builder()
                        .flushSecs(persistInMemoryEmbeddingStoreSettings.getFlushSecs())
                        .persistFile(vectorFile)
                        .build();
            case PGVectorEmbeddingStore:
                PGVectorEmbeddingStoreSettings pgVectorEmbeddingStoreSettings = KVSettingUtil.kvSettingsToObject(
                        siloEntity.getVectorStoreSettings(), PGVectorEmbeddingStoreSettings.class);
                return PgVectorEmbeddingStore
                        .builder()
                        .host(pgVectorEmbeddingStoreSettings.getHost())
                        .port(pgVectorEmbeddingStoreSettings.getPort())
                        .database(pgVectorEmbeddingStoreSettings.getDatabase())
                        .user(pgVectorEmbeddingStoreSettings.getUser())
                        .password(pgVectorEmbeddingStoreSettings.getPassword())
                        .table(String.format("dragon_silo_%s", siloEntity.getUuid().toString().replace("-", "_")))
                        .createTable(true)
                        .dimension(siloEntity.getEmbeddingModel().getModelDefinition().getDimensions())
                        .build();
            default:
                throw new UnsupportedOperationException(
                        String.format("VectorStoreType not supported : %s", siloEntity.getVectorStore()));
        }
    }
}
