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

    public static Try withLogLevel(Level logLevel) {
        return new Try().logLevel(logLevel);
    }

    public Try logLevel(Level logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public static Try withTimeout(Integer timeout, TimeUnit timeUnit) {
        return new Try().timeout(timeout, timeUnit);
    }

    public Try timeout(Integer timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;

        return this;
    }

    public static Try withRethrow(Boolean rethrow) {
        return new Try().rethrow(rethrow);
    }

    public Try rethrow(Boolean rethrow) {
        this.rethrow = rethrow;

        return this;
    }

    public static void thisBlock(ExceptionalRunnable runnable) {
        new Try().run(runnable);
    }

    // Accept a Runnable and return void
    // If an exception is thrown, log it
    // If timeout occurs, log it
    public void run(ExceptionalRunnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T thisBlock(Callable<T> callable) {
        return new Try().run(callable);
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
