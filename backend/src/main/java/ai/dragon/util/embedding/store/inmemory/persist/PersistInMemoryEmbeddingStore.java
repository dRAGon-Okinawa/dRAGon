package ai.dragon.util.embedding.store.inmemory.persist;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.dragon.properties.store.PersistInMemoryEmbeddingStoreSettings;
import ai.dragon.util.Debouncer;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.store.embedding.CosineSimilarity;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.RelevanceScore;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.Builder;

// Based on :
// https://raw.githubusercontent.com/langchain4j/langchain4j/main/langchain4j/src/main/java/dev/langchain4j/store/embedding/inmemory/InMemoryEmbeddingStore.java
@Builder
public class PersistInMemoryEmbeddingStore implements EmbeddingStore<TextSegment> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CopyOnWriteArrayList<PersistInMemoryEntry<TextSegment>> entries = new CopyOnWriteArrayList<>();
    private final Debouncer debouncer = new Debouncer();

    private File persistFile;
    private PersistInMemoryEmbeddingStoreSettings settings;

    /*
     * private PersistInMemoryEmbeddingStore() {
     * }
     * 
     * public static PersistInMemoryEmbeddingStore createFromFileAndSettings(File
     * persistFile,
     * PersistInMemoryEmbeddingStoreSettings settings) {
     * PersistInMemoryEmbeddingStore store = new PersistInMemoryEmbeddingStore();
     * store.persistFile = persistFile;
     * store.restoreFromFileNow();
     * return store;
     * }
     */

    // TODO Remove funcs...

    public void flushToDisk() {
        if (settings == null) {
            return;
        }
        debouncer.debounce(Void.class, new Runnable() {
            @Override
            public void run() {
                if (persistFile == null) {
                    return;
                }
                flushToDiskNow();
            }
        }, settings.getFlushSecs(), TimeUnit.SECONDS);
    }

    public PersistInMemoryEmbeddingStore restoreFromFile() {
        try {
            logger.debug(String.format("Restoring embeddings from file : %s", persistFile));
            if (persistFile.exists()) {
                String json = new String(Files.readAllBytes(persistFile.toPath()));
                CopyOnWriteArrayList<PersistInMemoryEntry<TextSegment>> restoredEntries = codec().fromJson(json);
                entries.addAll(restoredEntries);
                logger.info(String.format("Restored %d embeddings from file : %s", entries.size(), persistFile));
            } else {
                logger.info(String.format("No embeddings found in file : %s", persistFile));
            }
        } catch (Exception ex) {
            logger.error(String.format("Failed to restore from file : %s", persistFile), ex);
            throw new RuntimeException(ex);
        }
        return this;
    }

    @Override
    public String add(Embedding embedding) {
        String id = Utils.randomUUID();
        add(id, embedding);
        return id;
    }

    @Override
    public void add(String id, Embedding embedding) {
        add(id, embedding, null);
    }

    @Override
    public String add(Embedding embedding, TextSegment embedded) {
        String id = Utils.randomUUID();
        add(id, embedding, embedded);
        return id;
    }

    public void add(String id, Embedding embedding, TextSegment embedded) {
        entries.add(new PersistInMemoryEntry<>(id, embedding, embedded));
        flushToDisk();
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        List<PersistInMemoryEntry<TextSegment>> newEntries = embeddings.stream()
                .map(embedding -> new PersistInMemoryEntry<TextSegment>(Utils.randomUUID(), embedding))
                .collect(Collectors.toList());
        return add(newEntries);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        if (embeddings.size() != embedded.size()) {
            throw new IllegalArgumentException("The list of embeddings and embedded must have the same size");
        }
        List<PersistInMemoryEntry<TextSegment>> newEntries = IntStream.range(0, embeddings.size())
                .mapToObj(i -> new PersistInMemoryEntry<>(Utils.randomUUID(), embeddings.get(i), embedded.get(i)))
                .collect(Collectors.toList());
        return add(newEntries);
    }

    private List<String> add(List<PersistInMemoryEntry<TextSegment>> newEntries) {
        entries.addAll(newEntries);
        flushToDisk();
        return newEntries.stream()
                .map(entry -> entry.getId())
                .collect(Collectors.toList());
    }

    private void flushToDiskNow() {
        if (persistFile == null) {
            logger.warn("Won't flush to disk because persistFile is null.");
            return;
        }
        logger.debug(String.format("Flushing %d embeddings to file : %s", entries.size(), persistFile));
        try {
            String json = codec().toJson(this.entries);
            Files.write(persistFile.toPath(), json.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception ex) {
            logger.error(String.format("Failed to flush to file : %s", persistFile), ex);
            throw new RuntimeException(ex);
        }
    }

    private static GsonPersistInMemoryEmbeddingStoreJsonCodec codec() {
        return new GsonPersistInMemoryEmbeddingStoreJsonCodec();
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest embeddingSearchRequest) {
        Comparator<EmbeddingMatch<TextSegment>> comparator = Comparator.comparingDouble(EmbeddingMatch::score);
        PriorityQueue<EmbeddingMatch<TextSegment>> matches = new PriorityQueue<>(comparator);
        Filter filter = embeddingSearchRequest.filter();
        for (PersistInMemoryEntry<TextSegment> entry : entries) {
            if (filter != null && entry.getEmbedded() instanceof TextSegment) {
                Metadata metadata = ((TextSegment) entry.getEmbedded()).metadata();
                if (!filter.test(metadata)) {
                    continue;
                }
            }
            double cosineSimilarity = CosineSimilarity.between(entry.getEmbedding(),
                    embeddingSearchRequest.queryEmbedding());
            double score = RelevanceScore.fromCosineSimilarity(cosineSimilarity);
            if (score >= embeddingSearchRequest.minScore()) {
                matches.add(new EmbeddingMatch<>(score, entry.getId(), entry.getEmbedding(), entry.getEmbedded()));
                if (matches.size() > embeddingSearchRequest.maxResults()) {
                    matches.poll();
                }
            }
        }
        List<EmbeddingMatch<TextSegment>> result = new ArrayList<>(matches);
        result.sort(comparator);
        Collections.reverse(result);
        return new EmbeddingSearchResult<>(result);
    }
}
