package ai.dragon.util.db.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.common.tuples.Pair;
import org.dizitart.no2.exceptions.FilterException;
import org.junit.jupiter.api.Test;

class ExtendedRegexFilterTest {
    @Test
    void testConstructorWithPattern() {
        ExtendedRegexFilter filter = new ExtendedRegexFilter("field", "test");
        assertNotNull(filter);
    }

    @Test
    void testConstructorWithPatternAndFlags() {
        ExtendedRegexFilter filter = new ExtendedRegexFilter("field", "test", Pattern.CASE_INSENSITIVE);
        assertNotNull(filter);
    }

    @Test
    void testApplyWithMatchingStringField() {
        Document document = Document.createDocument("field", "testValue");
        ExtendedRegexFilter filter = new ExtendedRegexFilter("field", "test");
        Pair<NitriteId, Document> pair = new Pair<>(NitriteId.newId(), document);

        assertTrue(filter.apply(pair));
    }

    @Test
    void testApplyWithNonMatchingStringField() {
        Document document = Document.createDocument("field", "noMatch");
        ExtendedRegexFilter filter = new ExtendedRegexFilter("field", "test");
        Pair<NitriteId, Document> pair = new Pair<>(NitriteId.newId(), document);

        assertFalse(filter.apply(pair));
    }

    @Test
    void testApplyWithNonStringField() {
        Document document = Document.createDocument("field", 123);
        ExtendedRegexFilter filter = new ExtendedRegexFilter("field", "test");
        Pair<NitriteId, Document> pair = new Pair<>(NitriteId.newId(), document);

        assertThrows(FilterException.class, () -> filter.apply(pair));
    }

    @Test
    void testApplyWithNullFieldValue() {
        Document document = Document.createDocument("field", null);
        ExtendedRegexFilter filter = new ExtendedRegexFilter("field", "test");
        Pair<NitriteId, Document> pair = new Pair<>(NitriteId.newId(), document);

        assertFalse(filter.apply(pair));
    }

    @Test
    void testToString() {
        ExtendedRegexFilter filter = new ExtendedRegexFilter("field", "test");
        assertEquals("(field regex test)", filter.toString());
    }
}
