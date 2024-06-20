package ai.dragon.util.fluenttry;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class Try {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer timeout;
    private TimeUnit timeUnit;
    private Level logLevel;
    private Boolean rethrow;

    private Try() {
        timeout = null;
        logLevel = Level.ERROR;
    }

    public static Try create() {
        return new Try();
    }

    public Try withLogLevel(Level logLevel) {
        this.logLevel = logLevel;

        return this;
    }

    public Try withTimeout(Integer timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;

        return this;
    }

    public Try withRethrow(Boolean rethrow) {
        this.rethrow = rethrow;

        return this;
    }

    public void run(ExceptionalRunnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    // Accept a Callable and return the result of the Callable
    // If an exception is thrown, log it and return null
    // If timeout occurs, log it and return null
    public <T> T run(Callable<T> callable) {
        T result = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Exception ex = null;
        try {
            if (timeout != null && timeUnit != null) {
                result = executorService.submit(callable).get(timeout, timeUnit);
            } else {
                result = executorService.submit(callable).get();
            }
        } catch (InterruptedException e) {
            ex = e;
            logger.atLevel(logLevel).log("Try Callable has been interrupted", ex);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            ex = e;
            logger.atLevel(logLevel).log("Try Callable has thrown an Execution Exception", ex);
        } catch (TimeoutException e) {
            ex = e;
            logger.atLevel(logLevel).log("Try Callable has timed out", ex);
        } catch (Exception e) {
            ex = e;
            logger.atLevel(logLevel).log("Try Callable has thrown an exception", ex);
        }
        if (Boolean.TRUE.equals(rethrow) && ex != null) {
            throw new RuntimeException(ex);
        }
        executorService.shutdown();
        return result;
    }
}
