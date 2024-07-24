package ai.dragon.util.spel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;

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
}
