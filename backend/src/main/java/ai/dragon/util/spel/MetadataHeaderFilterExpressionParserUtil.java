package ai.dragon.util.spel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;

public class MetadataHeaderFilterExpressionParserUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsExpressionParserUtil.class);

    public static Filter parse(String metadataHeaderExpression) {
        if (metadataHeaderExpression == null || metadataHeaderExpression.isEmpty()) {
            return null;
        }
        try {
            if (!metadataHeaderExpression.matches("\\{\\{.*?\\}\\}")) {
                LOGGER.warn("Metadata header expression does not match the expected format: " + metadataHeaderExpression);
                return null;
            }
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
            context.setVariable("metadataKey", MetadataFilterBuilder.class.getMethod("metadataKey", String.class));
            Expression exp = parser.parseExpression(metadataHeaderExpression);
            return exp.getValue(context, Filter.class);
        } catch (Exception ex) {
            LOGGER.warn("Error parsing expression: " + metadataHeaderExpression, ex);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(parse("{{ #metadataKey('document_name').isEqualTo('SunSpots.pdf') }}"));
    }
}
