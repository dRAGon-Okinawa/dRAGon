package ai.dragon.util.spel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsIn;

@ActiveProfiles("test")
public class MetadataHeaderFilterExpressionParserUtilTest {
    @Test
    void isEqualToExpression() {
        Filter filter = MetadataHeaderFilterExpressionParserUtil
                .parse("{{ #metadataKey('document_name').isEqualTo('SunSpots.pdf') }}");
        assertNotNull(filter);
        assertEquals(true, filter instanceof IsEqualTo);

        IsEqualTo isEqualToFilter = (IsEqualTo) filter;
        assertEquals("document_name", isEqualToFilter.key());
        assertEquals("SunSpots.pdf", isEqualToFilter.comparisonValue());
    }

    @Test
    void isInExpression() {
        Filter filter = MetadataHeaderFilterExpressionParserUtil
                .parse("{{ #metadataKey('document_name').isIn('SunSpots.pdf', 'viewing_sun_safely.pdf') }}");
        assertNotNull(filter);
        assertEquals(true, filter instanceof IsIn);

        IsIn isInFilter = (IsIn) filter;
        Iterator<?> it = isInFilter.comparisonValues().iterator();
        assertEquals("document_name", isInFilter.key());
        assertEquals(2, isInFilter.comparisonValues().size());
        assertEquals("SunSpots.pdf", it.next());
        assertEquals("viewing_sun_safely.pdf", it.next());
    }

    @Test
    void invalidExpression() {
        Filter filter = MetadataHeaderFilterExpressionParserUtil.parse("{{ #metadataKey('document_name').isIn() }}");
        assertEquals(null, filter);
    }

    @Test
    void nullExpression() {
        Filter filter = MetadataHeaderFilterExpressionParserUtil.parse(null);
        assertEquals(null, filter);
    }

    @Test
    void emptyExpression() {
        Filter filter = MetadataHeaderFilterExpressionParserUtil.parse("");
        assertEquals(null, filter);
    }
}
