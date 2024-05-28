package ai.dragon.enumeration;

public enum ProviderType {
    ONNX("ONNX"),
    OpenAI("OpenAI");

    private String value;

    ProviderType(String value) {
        this.value = value;
    }

    public static ProviderType fromString(String text) {
        for (ProviderType provider : ProviderType.values()) {
            if (provider.value.equalsIgnoreCase(text)) {
                return provider;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
