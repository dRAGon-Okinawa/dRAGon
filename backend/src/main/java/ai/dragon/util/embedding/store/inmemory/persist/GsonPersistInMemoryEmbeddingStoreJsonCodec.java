package ai.dragon.util.embedding.store.inmemory.persist;

import java.lang.reflect.Type;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import dev.langchain4j.data.segment.TextSegment;

public class GsonPersistInMemoryEmbeddingStoreJsonCodec {
    private static final Gson GSON = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    private static final Type TYPE = new TypeToken<CopyOnWriteArrayList<PersistInMemoryEntry<TextSegment>>>() {
    }.getType();

    public CopyOnWriteArrayList<PersistInMemoryEntry<TextSegment>> fromJson(String json) {
        return GSON.fromJson(json, TYPE);
    }

    public String toJson(CopyOnWriteArrayList<PersistInMemoryEntry<TextSegment>> entries) {
        return GSON.toJson(entries);
    }
}
