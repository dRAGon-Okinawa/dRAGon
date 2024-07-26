package ai.dragon.test.junit.extension.retry;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class RetryOnExceptionsExtension implements TestExecutionExceptionHandler, BeforeEachCallback {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Method testMethod = context.getRequiredTestMethod();
        RetryOnExceptions retry = testMethod.getAnnotation(RetryOnExceptions.class);

        if (retry != null) {
            int maxRetries = retry.value();
            Class<? extends Throwable>[] retryOnExceptions = retry.onExceptions();
            Store store = context.getStore(ExtensionContext.Namespace.create(testMethod));

            int currentRetries = store.getOrDefault("retries", Integer.class, 0);

            boolean shouldRetry = false;
            for (Class<? extends Throwable> retryOnException : retryOnExceptions) {
                if (retryOnException.isInstance(throwable)) {
                    shouldRetry = true;
                    break;
                }
            }

            if (shouldRetry && currentRetries < maxRetries) {
                store.put("retries", currentRetries + 1);
                context.getRequiredTestMethod().invoke(context.getRequiredTestInstance());
            } else {
                throw throwable;
            }
        } else {
            throw throwable;
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        if (testMethod.isAnnotationPresent(RetryOnExceptions.class)) {
            Store store = context.getStore(ExtensionContext.Namespace.create(testMethod));
            store.put("retries", 0); // Reset retry count before each test
        }
    }
}