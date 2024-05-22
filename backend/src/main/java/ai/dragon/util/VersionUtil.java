package ai.dragon.util;

public class VersionUtil {
    public static String getVersion() {
        if (System.getProperty("app.version") != null) {
            return System.getProperty("app.version");
        }

        return VersionUtil.class.getPackage().getImplementationVersion();
    }
}
