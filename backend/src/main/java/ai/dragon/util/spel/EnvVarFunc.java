package ai.dragon.util.spel;

public class EnvVarFunc {
    public static String env(String key) {
        return System.getenv(key);
    }
}
