package ai.dragon.enumeration;

public enum GranaryEngine {
    SearXNG("SearXNG");

    private String value;

    GranaryEngine(String value) {
        this.value = value;
    }

    public static GranaryEngine fromString(String text) {
        for (GranaryEngine type : GranaryEngine.values()) {
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
