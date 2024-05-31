package ai.dragon.enumeration;

public enum IngestorType {
    FileSystem("FileSystem");

    private String value;

    IngestorType(String value) {
        this.value = value;
    }

    public static IngestorType fromString(String text) {
        for (IngestorType type : IngestorType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
