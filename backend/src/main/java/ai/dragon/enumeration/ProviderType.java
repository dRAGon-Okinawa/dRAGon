package ai.dragon.enumeration;

public enum ProviderType {
    OPENAI("openai");

    private String value;

    ProviderType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
