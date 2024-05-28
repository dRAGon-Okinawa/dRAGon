package ai.dragon.enumeration;

public enum IngestorType {
    LOCAL("LOCAL");

    private String value;

    IngestorType(String value) {
        this.value = value;
    }

    public static IngestorType fromString(String text) {
        for (IngestorType ingestor : IngestorType.values()) {
            if (ingestor.value.equalsIgnoreCase(text)) {
                return ingestor;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
