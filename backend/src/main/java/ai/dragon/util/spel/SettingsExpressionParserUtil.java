package ai.dragon.util.spel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import ai.dragon.util.spel.func.EnvVarFunc;

public class SettingsExpressionParserUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsExpressionParserUtil.class);

    public static <T> T parse(String expression, Class<T> clazz) {
        return parse(expression, null, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(String expression, T fallbackValue, Class<T> clazz) {
        if (expression == null || expression.isEmpty()) {
            return fallbackValue;
        }
        try {
            if (!expression.matches("\\{\\{.*?\\}\\}")) {
                return (T) expression;
            }
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
            context.setVariable("env", EnvVarFunc.class.getMethod("env", String.class));
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(context, clazz);
        } catch (Exception ex) {
            LOGGER.warn("Error parsing expression: " + expression, ex);
            return fallbackValue;
        }
    }
}
