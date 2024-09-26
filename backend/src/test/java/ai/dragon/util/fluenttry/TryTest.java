package ai.dragon.util.fluenttry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class TryTest {
    @Test
    void tryThisblock() {
        String toBeReturned = "dRAGon";
        String result = Try.thisBlock(() -> {
            return toBeReturned;
        });
        assertEquals(toBeReturned, result);
    }

    @Test
    void tryTimeout() {
        assertThrows(RuntimeException.class, () -> {
            Try
                    .withTimeout(100, TimeUnit.MILLISECONDS)
                    .rethrow(true)
                    .run(() -> {
                        Thread.sleep(500);
                    });
        });
    }

    @Test
    void tryTimeoutFallback() {
        String fallback = "fallback";
        String result = Try
                .withFallback(exception -> {
                    return fallback;
                })
                .timeout(100, TimeUnit.MILLISECONDS)
                .run(() -> {
                    Thread.sleep(500);
                    return "Should not be returned";
                });
        assertEquals(fallback, result);

    }

    @Test
    void tryFallback() {
        String fallback = "fallback";
        String result = Try
                .withFallback(exception -> {
                    return fallback;
                })
                .run(() -> {
                    throw new RuntimeException();
                });
        assertEquals(fallback, result);
    }
}
