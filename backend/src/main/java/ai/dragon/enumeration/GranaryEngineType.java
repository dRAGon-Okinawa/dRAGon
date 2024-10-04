package ai.dragon.enumeration;

public enum GranaryEngineType {
    WebSearchEngine("WebSearchEngine");

    private String value;

    GranaryEngineType(String value) {
        this.value = value;
    }

    public static GranaryEngineType fromString(String text) {
        for (GranaryEngineType type : GranaryEngineType.values()) {
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
