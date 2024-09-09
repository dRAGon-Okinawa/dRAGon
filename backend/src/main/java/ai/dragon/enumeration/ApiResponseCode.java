package ai.dragon.enumeration;

public enum ApiResponseCode {
    SUCCESS("0000"),
    DUPLICATES("0422");

    private String value;

    ApiResponseCode(String value) {
        this.value = value;
    }

    public static ApiResponseCode fromString(String text) {
        for (ApiResponseCode b : ApiResponseCode.values()) {
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
