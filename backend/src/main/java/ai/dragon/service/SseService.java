package ai.dragon.service;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
    // TODO Custom timeout :
    public static final long DEFAULT_TIMEOUT = 90L * 1000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public UUID createEmitter() {
        return createEmitter(DEFAULT_TIMEOUT);
    }

    public UUID createEmitter(Long timeout) {
        UUID id = UUID.randomUUID();
        SseEmitter emitter = new SseEmitter(timeout);
        addEmitter(id, emitter);
        return id;
    }

    public void addEmitter(UUID id, SseEmitter emitter) {
        emitters.put(id, emitter);
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
    }

    public SseEmitter retrieveEmitter(UUID id) {
        return emitters.get(id);
    }

    public boolean complete(UUID id) {
        SseEmitter emitter = emitters.get(id);
        if (emitter == null) {
            logger.warn("Can't complete : No emitter found for id '{}'", id);
            return false;
        }
        emitter.complete();
        emitters.remove(id);
        return true;
    }

    public boolean sendEvent(UUID id, Object event) {
        SseEmitter emitter = emitters.get(id);
        if (emitter == null) {
            logger.info(event.toString());
            logger.warn("Can't send event : No emitter found for id '{}'", id);
            return false;
        }
        try {
            emitter.send(event);
        } catch (IOException e) {
            emitter.complete();
            emitters.remove(id);
            return false;
        }
        return true;
    }
}
