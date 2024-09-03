package ai.dragon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class UUIDUtilTest {
    @Test
    void zeroUUID_returnsZeroUUID() {
        UUID uuid = UUIDUtil.zeroUUID();
        assertEquals(0, uuid.getMostSignificantBits());
        assertEquals(0, uuid.getLeastSignificantBits());
    }
    
    @Test
    void zeroUUIDString_returnsZeroUUIDString() {
        String uuidString = UUIDUtil.zeroUUIDString();
        assertEquals("00000000-0000-0000-0000-000000000000", uuidString);
    }
}
