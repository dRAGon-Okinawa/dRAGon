package ai.dragon.controller.api.backendapi.repository;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.dizitart.no2.exceptions.UniqueConstraintException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import ai.dragon.entity.AbstractEntity;
import ai.dragon.repository.AbstractRepository;

abstract class AbstractCrudBackendApiController<T extends AbstractEntity> {
    public T update(String uuid, Map<String, Object> fields, AbstractRepository<T> repository) {
        T entityToUpdate = repository.getByUuid(uuid);
        if (entityToUpdate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String fieldsAsJson = objectMapper.writeValueAsString(fields);
            ObjectReader objectReader = objectMapper.readerForUpdating(entityToUpdate);
            entityToUpdate = objectReader.readValue(fieldsAsJson);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error parsing fields as JSON", ex);
        }
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
        try {
            repository.save(entity);
        } catch (UniqueConstraintException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unique key constraint violation", ex);
        }
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
