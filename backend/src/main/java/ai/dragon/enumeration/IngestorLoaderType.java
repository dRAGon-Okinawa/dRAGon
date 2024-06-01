package ai.dragon.enumeration;

public enum IngestorLoaderType {
    FileSystem("FileSystem");

    private String value;

    IngestorLoaderType(String value) {
        this.value = value;
    }

    public static IngestorLoaderType fromString(String text) {
        for (IngestorLoaderType type : IngestorLoaderType.values()) {
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
