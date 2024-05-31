package ai.dragon.enumeration;

public enum VectorStoreType {
    // TODO : Add more VectorStoreType
    // PGVector : PgVectorEmbeddingStore
    InMemoryEmbeddingStore("InMemoryEmbeddingStore");

    private String value;

    VectorStoreType(String value) {
        this.value = value;
    }

    public static VectorStoreType fromString(String text) {
        for (VectorStoreType vectorStore : VectorStoreType.values()) {
            if (vectorStore.value.equalsIgnoreCase(text)) {
                return vectorStore;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
