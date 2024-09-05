package ai.dragon.util.db.filters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.dizitart.no2.filters.NitriteFilter;
import org.junit.jupiter.api.Test;

public class ExtendedFluentFilterTest {
    @Test
    void testRegex() {
        ExtendedFluentFilter filter = ExtendedFluentFilter.where("fieldName");
        NitriteFilter nitriteFilter = filter.regex(".*test.*");
        assertNotNull(nitriteFilter);
        assertTrue(nitriteFilter.toString().contains(".*test.*"));
    }

    @Test
    void testRegexWithFlags() {
        ExtendedFluentFilter filter = ExtendedFluentFilter.where("fieldName");
        NitriteFilter nitriteFilter = filter.regex(".*test.*", Pattern.CASE_INSENSITIVE);
        assertNotNull(nitriteFilter);
        assertTrue(nitriteFilter.toString().contains(".*test.*"));
    }

    @Test
    void testNonsensitiveRegex() {
        ExtendedFluentFilter filter = ExtendedFluentFilter.where("fieldName");
        NitriteFilter nitriteFilter = filter.nonsensitiveRegex(".*test.*");
        assertNotNull(nitriteFilter);
        assertTrue(nitriteFilter.toString().contains(".*test.*"));
    }
}
