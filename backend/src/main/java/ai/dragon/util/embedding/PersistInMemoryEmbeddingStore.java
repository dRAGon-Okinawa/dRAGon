package ai.dragon.util.embedding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.dragon.util.Debouncer;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.internal.ValidationUtils;
import dev.langchain4j.store.embedding.CosineSimilarity;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.RelevanceScore;
import dev.langchain4j.store.embedding.filter.Filter;

// Based on :
// https://raw.githubusercontent.com/langchain4j/langchain4j/main/langchain4j/src/main/java/dev/langchain4j/store/embedding/inmemory/InMemoryEmbeddingStore.java
public class PersistInMemoryEmbeddingStore<Embedded> implements EmbeddingStore<Embedded> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CopyOnWriteArrayList<Entry<Embedded>> entries = new CopyOnWriteArrayList<>();
    private final Debouncer debouncer = new Debouncer();

    private File persistFile;
    private Integer flushSecs;

    private PersistInMemoryEmbeddingStore() {
    }

    public static <Embedded> PersistInMemoryEmbeddingStore<Embedded> createFromFile(File persistFile,
            Integer flushSecs) {
        PersistInMemoryEmbeddingStore<Embedded> store = new PersistInMemoryEmbeddingStore<>();
        store.persistFile = persistFile;
        store.flushSecs = flushSecs;
        store.flushSecs = 5;
        return store;
    }

    // TODO Remove funcs...

    public void flushToDisk() {
        debouncer.debounce(Void.class, new Runnable() {
            @Override
            public void run() {
                flushToDiskNow();
            }
        }, flushSecs, TimeUnit.SECONDS);
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
    public String add(Embedding embedding, Embedded embedded) {
        String id = Utils.randomUUID();
        add(id, embedding, embedded);
        return id;
    }

    public void add(String id, Embedding embedding, Embedded embedded) {
        entries.add(new Entry<>(id, embedding, embedded));
        flushToDisk();
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        List<Entry<Embedded>> newEntries = embeddings.stream()
                .map(embedding -> new Entry<Embedded>(Utils.randomUUID(), embedding))
                .collect(Collectors.toList());
        return add(newEntries);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<Embedded> embedded) {
        if (embeddings.size() != embedded.size()) {
            throw new IllegalArgumentException("The list of embeddings and embedded must have the same size");
        }
        List<Entry<Embedded>> newEntries = IntStream.range(0, embeddings.size())
                .mapToObj(i -> new Entry<>(Utils.randomUUID(), embeddings.get(i), embedded.get(i)))
                .collect(Collectors.toList());
        return add(newEntries);
    }

    private List<String> add(List<Entry<Embedded>> newEntries) {
        entries.addAll(newEntries);
        flushToDisk();
        return newEntries.stream()
                .map(entry -> entry.id)
                .collect(Collectors.toList());
    }

    private void flushToDiskNow() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(persistFile, entries);
        } catch (Exception ex) {
            logger.error(String.format("Failed to flush to file : %s", persistFile), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public EmbeddingSearchResult<Embedded> search(EmbeddingSearchRequest embeddingSearchRequest) {
        Comparator<EmbeddingMatch<Embedded>> comparator = Comparator.comparingDouble(EmbeddingMatch::score);
        PriorityQueue<EmbeddingMatch<Embedded>> matches = new PriorityQueue<>(comparator);
        Filter filter = embeddingSearchRequest.filter();
        for (Entry<Embedded> entry : entries) {
            if (filter != null && entry.embedded instanceof TextSegment) {
                Metadata metadata = ((TextSegment) entry.embedded).metadata();
                if (!filter.test(metadata)) {
                    continue;
                }
            }
            double cosineSimilarity = CosineSimilarity.between(entry.embedding,
                    embeddingSearchRequest.queryEmbedding());
            double score = RelevanceScore.fromCosineSimilarity(cosineSimilarity);
            if (score >= embeddingSearchRequest.minScore()) {
                matches.add(new EmbeddingMatch<>(score, entry.id, entry.embedding, entry.embedded));
                if (matches.size() > embeddingSearchRequest.maxResults()) {
                    matches.poll();
                }
            }
        }
        List<EmbeddingMatch<Embedded>> result = new ArrayList<>(matches);
        result.sort(comparator);
        Collections.reverse(result);
        return new EmbeddingSearchResult<>(result);
    }

    private static class Entry<Embedded> {
        String id;
        Embedding embedding;
        Embedded embedded;

        Entry(String id, Embedding embedding) {
            this(id, embedding, null);
        }

        Entry(String id, Embedding embedding, Embedded embedded) {
            this.id = ValidationUtils.ensureNotBlank(id, "id");
            this.embedding = ValidationUtils.ensureNotNull(embedding, "embedding");
            this.embedded = embedded;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Entry<?> that = (Entry<?>) o;
            return Objects.equals(this.id, that.id)
                    && Objects.equals(this.embedding, that.embedding)
                    && Objects.equals(this.embedded, that.embedded);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, embedding, embedded);
        }
    }
}
