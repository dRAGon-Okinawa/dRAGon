package ai.dragon.listener;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.dizitart.no2.collection.events.CollectionEventListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.dragon.entity.AbstractEntity;

public class EntityChangeListener<T extends AbstractEntity> implements CollectionEventListener {
    public void onEvent(CollectionEventInfo<?> collectionEventInfo) {
        try {
            Object document = collectionEventInfo.getItem();
            ObjectMapper objectMapper = new ObjectMapper();
            T entity = objectMapper.convertValue(document, getGenericSuperclass());
            onChangeEvent((CollectionEventInfo<?>) collectionEventInfo, entity.getUuid());
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public void onChangeEvent(CollectionEventInfo<?> collectionEventInfo, UUID uuid) {
    }

    @SuppressWarnings("unchecked")
    public Class<T> getGenericSuperclass() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) superclass.getActualTypeArguments()[0];
    }
}
