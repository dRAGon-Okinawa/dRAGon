package ai.dragon.test.junit.extension.retry;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RetryOnExceptionsExtension.class)
public @interface RetryOnExceptions {
    int value() default 3; // Default retry count
    Class<? extends Throwable>[] onExceptions() default { Throwable.class }; // Exceptions to retry on
}