package ai.dragon.enumeration;

public enum SiloType {
    LOCAL("LOCAL");

    private String value;

    SiloType(String value) {
        this.value = value;
    }

    public static SiloType fromString(String text) {
        for (SiloType silo : SiloType.values()) {
            if (silo.value.equalsIgnoreCase(text)) {
                return silo;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
