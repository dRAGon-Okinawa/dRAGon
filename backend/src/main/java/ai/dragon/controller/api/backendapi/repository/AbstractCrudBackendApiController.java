package ai.dragon.controller.api.backendapi.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.IAbstractEntity;
import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.AbstractRepository;

abstract class AbstractCrudBackendApiController<T extends IAbstractEntity> {
    public T update(String uuid, Map<String, Object> fields, AbstractRepository<T> repository) {
        T entityToUpdate = repository.getByUuid(uuid);
        if (entityToUpdate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(SiloEntity.class, k);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, entityToUpdate, v);
            }
        });

        if (!uuid.equals(entityToUpdate.getUuid().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UUID must not be changed.");
        }

        repository.save(entityToUpdate);

        return entityToUpdate;
    }

    public List<T> list(AbstractRepository<T> repository) {
        return repository.find().toList();
    }

    public T create(AbstractRepository<T> repository) throws Exception {
        return create(repository, null);
    }

    public T create(AbstractRepository<T> repository, Function<T, T> beforeSaveCallback) throws Exception {
        Class<T> clazz = repository.getGenericSuperclass();
        Constructor<T> cons = clazz.getConstructor();
        T entity = cons.newInstance();
        if (beforeSaveCallback != null) {
            entity = beforeSaveCallback.apply(entity);
        }
        repository.save(entity);
        return entity;
    }

    public T get(String uuid, AbstractRepository<T> repository) {
        return Optional.ofNullable(repository.getByUuid(uuid))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found."));
    }

    public void delete(String uuid, AbstractRepository<T> repository) {
        if (!repository.exists(uuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found.");
        }
        repository.delete(uuid);
    }
}
