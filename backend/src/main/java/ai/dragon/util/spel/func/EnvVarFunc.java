package ai.dragon.util.spel.func;

public class EnvVarFunc {
    public static String env(String key) {
        return System.getenv(key);
    }
}
