package ai.dragon.util.embedding.store.inmemory.persist;

import java.util.Objects;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.internal.ValidationUtils;
import lombok.Getter;

@Getter
public class PersistInMemoryEntry<Embedded> {
    private String id;
    private Embedding embedding;
    private Embedded embedded;

    public PersistInMemoryEntry(String id, Embedding embedding) {
        this(id, embedding, null);
    }

    public PersistInMemoryEntry(String id, Embedding embedding, Embedded embedded) {
        this.id = ValidationUtils.ensureNotBlank(id, "id");
        this.embedding = ValidationUtils.ensureNotNull(embedding, "embedding");
        this.embedded = embedded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistInMemoryEntry<?> that = (PersistInMemoryEntry<?>) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.embedding, that.embedding)
                && Objects.equals(this.embedded, that.embedded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, embedding, embedded);
    }
}
