package ai.dragon.junit.extension.retry;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryingTestExtension implements TestExecutionExceptionHandler, BeforeTestExecutionCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryingTestExtension.class);
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("dragon", "test",
            "retry");

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        // Reset retry count before each test execution
        context.getStore(NAMESPACE).put("retryCount", 0);
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Method testMethod = context.getRequiredTestMethod();
        RetryingTest retryingTest = testMethod.getAnnotation(RetryingTest.class);

        if (retryingTest != null) {
            int maxTries = retryingTest.maxTries();
            int retryWaitMs = retryingTest.retryWaitMs();
            int beforeWaitMs = retryingTest.beforeWaitMs();
            ExtensionContext.Store store = context.getStore(NAMESPACE);
            Integer retryCount = store.get("retryCount", Integer.class);
            if (retryCount == null) {
                retryCount = 0;
            }

            if (beforeWaitMs > 0 && retryCount == 0) {
                LOGGER.info("Waiting {}ms before executing test {}", beforeWaitMs, testMethod.getName());
                Thread.sleep(beforeWaitMs);
            }

            if (retryCount < maxTries) {
                store.put("retryCount", ++retryCount);
                try {
                    testMethod.invoke(context.getRequiredTestInstance());
                } catch (Throwable t) {
                    LOGGER.error("Retrying test {} (after {}) on exception: {}", testMethod.getName(), retryWaitMs, t);
                    if (retryWaitMs > 0) {
                        try {
                            Thread.sleep(retryWaitMs);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw e;
                        }
                    }
                    handleTestExecutionException(context, t);
                }
                return; // Return without throwing to retry the test
            }
        }
        throw throwable; // Rethrow exception after max retries
    }
}
