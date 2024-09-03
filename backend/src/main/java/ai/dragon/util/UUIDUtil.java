package ai.dragon.util;

import java.util.UUID;

public class UUIDUtil {
    private UUIDUtil() {
    }

    public static UUID zeroUUID() {
        return new UUID(0, 0);
    }

    public static String zeroUUIDString() {
        return zeroUUID().toString();
    }
}
