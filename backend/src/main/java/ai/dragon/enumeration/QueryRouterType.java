package ai.dragon.enumeration;

public enum QueryRouterType {
    DEFAULT("Default"),
    LANGUAGE_MODEL("LanguageModel");

    private String value;

    QueryRouterType(String value) {
        this.value = value;
    }

    public static QueryRouterType fromString(String text) {
        for (QueryRouterType b : QueryRouterType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
