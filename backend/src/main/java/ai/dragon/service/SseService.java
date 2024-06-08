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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public UUID createEmitter() {
        return createEmitter(10L * 1000L);
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

    public void complete(UUID id) {
        SseEmitter emitter = emitters.get(id);
        if (emitter == null) {
            logger.warn("No emitter found for id: {}", id);
            return;
        }
        emitter.complete();
        emitters.remove(id);
    }

    public void sendEvent(UUID id, Object event) {
        SseEmitter emitter = emitters.get(id);
        if (emitter == null) {
            logger.warn("No emitter found for id: {}", id);
            return;
        }
        try {
            emitter.send(event);
        } catch (IOException e) {
            emitter.complete();
            emitters.remove(id);
        }
    }
}
