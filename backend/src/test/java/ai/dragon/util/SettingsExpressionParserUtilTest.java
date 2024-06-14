package ai.dragon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class SettingsExpressionParserUtilTest {
    @Test
    void envExpression() {
        assertNotNull(SettingsExpressionParserUtil.parse("{{ #env('PATH') }}", String.class));
    }

    @Test
    void stringExpression() {
        assertEquals("dRAGon", SettingsExpressionParserUtil.parse("{{ 'dRAGon' }}", String.class));
        assertEquals("'dRAGon'", SettingsExpressionParserUtil.parse("'dRAGon'", String.class));
        assertEquals("dRAGon", SettingsExpressionParserUtil.parse("dRAGon", String.class));
    }

    @Test
    void integerExpression() {
        assertEquals(1985, SettingsExpressionParserUtil.parse("{{ 1985 }}", Integer.class));
        assertEquals("1985", SettingsExpressionParserUtil.parse("1985", String.class));
    }

    @Test
    void badFormatExpression() {
        assertNull(SettingsExpressionParserUtil.parse("{{ Awesome! }}", String.class));
        assertEquals("Alternative", SettingsExpressionParserUtil.parse("{{ Awesome! }}", "Alternative", String.class));
    }
}
