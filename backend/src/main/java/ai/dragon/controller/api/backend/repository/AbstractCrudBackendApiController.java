package ai.dragon.controller.api.backend.repository;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.exceptions.UniqueConstraintException;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.repository.Cursor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import ai.dragon.entity.AbstractEntity;
import ai.dragon.repository.AbstractRepository;
import ai.dragon.repository.util.Pager;

abstract class AbstractCrudBackendApiController<T extends AbstractEntity> {
    protected T update(String uuid, Map<String, Object> fields, AbstractRepository<T> repository) {
        T entityToUpdate = repository.getByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
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

    protected Cursor<T> findWithFilter(AbstractRepository<T> repository, Filter filter, FindOptions findOptions) {
        return repository.findWithFilter(filter, findOptions);
    }

    protected List<T> list(AbstractRepository<T> repository) {
        return repository.find().toList();
    }

    protected long count(AbstractRepository<T> repository) {
        return repository.find().size();
    }

    protected long count(AbstractRepository<T> repository, Filter filter) {
        return repository.findWithFilter(filter).size();
    }

    protected Pager<T> page(AbstractRepository<T> repository, int page, int size) {
        return this.page(repository, Filter.ALL, page, size);
    }

    protected Pager<T> page(AbstractRepository<T> repository, Filter filter, int page, int size) {
        Cursor<T> cursorFull = repository.findWithFilter(filter);
        Cursor<T> cursorLimit = repository.findWithFilter(filter, FindOptions.skipBy((page - 1) * size).limit(size));
        return Pager
                .<T>builder()
                .page(page)
                .size(size)
                .total(cursorFull.size())
                .data(cursorLimit.toList())
                .build();
    }

    protected T create(AbstractRepository<T> repository) throws Exception {
        return create(repository, null);
    }

    protected T create(AbstractRepository<T> repository, Function<T, T> beforeSaveCallback) throws Exception {
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

    protected T get(UUID uuid, AbstractRepository<T> repository) {
        return get(uuid.toString(), repository);
    }

    protected T get(String uuid, AbstractRepository<T> repository) {
        return repository.getByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found."));
    }

    protected void delete(UUID uuid, AbstractRepository<T> repository) {
        delete(uuid.toString(), repository);
    }

    protected void delete(String uuid, AbstractRepository<T> repository) {
        if (!repository.exists(uuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found.");
        }
        repository.delete(uuid);
    }
}
