package ai.dragon.junit.extension.retry;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class RetryingTestExtension implements TestExecutionExceptionHandler, BeforeTestExecutionCallback {
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("dRAGon", "test", "retry");
    
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
            int maxRetries = retryingTest.value();
            ExtensionContext.Store store = context.getStore(NAMESPACE);
            int retryCount = store.get("retryCount", Integer.class);

            if (retryCount < maxRetries) {
                store.put("retryCount", ++retryCount);
                try {
                    testMethod.invoke(context.getRequiredTestInstance());
                } catch (Throwable t) {
                    System.err.println("Retrying test " + testMethod.getName() + " after exception: " + t);
                    handleTestExecutionException(context, t);
                }
                return; // Return without throwing to retry the test
            }
        }
        throw throwable; // Rethrow exception after max retries
    }
}
