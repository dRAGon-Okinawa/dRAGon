package ai.dragon.util.fluenttry;

@FunctionalInterface
public interface ExceptionalRunnable {
    void run() throws Exception;
}
