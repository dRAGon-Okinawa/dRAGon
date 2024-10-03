package ai.dragon.junit.extension.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Test
public @interface RetryingTest {
    int maxTries() default 1; // Default retry count is 1
    int retryWaitMs() default 0; // Default retry wait time is 0ms
    int beforeWaitMs() default 0; // Default wait time before executing test is 0ms
}
