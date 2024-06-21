package ai.dragon.repository;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.dizitart.no2.collection.events.EventType;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.AbstractEntity;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.service.DatabaseService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Component
public abstract class AbstractRepository<T extends AbstractEntity> {
    @Autowired
    private DatabaseService databaseService;

    @SuppressWarnings("unchecked")
    public void save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is required");
        }

        if (entity.getUuid() == null) {
            throw new IllegalArgumentException("UUID is required");
        }

        // Throws an exception if the entity is not valid :
        this.validate(entity);

        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());

        if (exists(entity.getUuid())) {
            repository.update(entity);
        } else {
            repository.insert(entity);
        }
    }

    public boolean exists(String uuid) {
        return exists(UUID.fromString(uuid));
    }

    public boolean exists(UUID uuid) {
        return getByUuid(uuid).isPresent();
    }

    public Optional<T> getByUuid(String uuid) {
        return getByUuid(UUID.fromString(uuid));
    }

    public Optional<T> getByUuid(UUID uuid) {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return Optional.ofNullable(repository.getById(uuid));
    }

    public Cursor<T> find() {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return repository.find();
    }

    public Cursor<T> findWithFilter(Filter filter) {
        return findWithFilter(filter, null);
    }

    public Cursor<T> findWithFilter(Filter filter, FindOptions findOptions) {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return repository.find(filter, findOptions);
    }

    public Cursor<T> findByFieldValue(String fieldName, Object fieldValue) {
        return this.findByFieldValue(fieldName, fieldValue, null);
    }

    public Cursor<T> findByFieldValue(String fieldName, Object fieldValue, FindOptions findOptions) {
        return this.findWithFilter(FluentFilter.where(fieldName).eq(fieldValue), findOptions);
    }

    public Optional<T> findUniqueByFieldValue(String fieldName, Object fieldValue) {
        Cursor<T> cursor = this.findByFieldValue(fieldName, fieldValue);
        if (cursor.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Multiple entities found");
        }
        return cursor.size() == 1 ? Optional.of(cursor.firstOrNull()) : Optional.empty();
    }

    public Optional<T> findUniqueWithFilter(Filter filter) {
        return findUniqueWithFilter(filter, null);
    }

    public Optional<T> findUniqueWithFilter(Filter filter, FindOptions findOptions) {
        Cursor<T> cursor = this.findWithFilter(filter, findOptions);
        if (cursor.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Multiple entities found");
        }
        return cursor.size() == 1 ? Optional.of(cursor.firstOrNull()) : Optional.empty();
    }

    public void delete(String uuid) {
        delete(UUID.fromString(uuid));
    }

    public void delete(UUID uuid) {
        delete(getByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found")));
    }

    public void delete(T entity) {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.remove(entity);
    }

    public void deleteAll() {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.clear();
    }

    public long countAll() {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return repository.size();
    }

    public EntityChangeListener<T> subscribe(EntityChangeListener<T> listener) {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.subscribe(listener);
        return listener;
    }

    public EntityChangeListener<T> subscribe(EventType eventType, EntityChangeListener<T> listener) {
        EntityChangeListener<T> filterListener = new EntityChangeListener<T>() {
            @Override
            public void onEvent(CollectionEventInfo<?> collectionEventInfo) {
                if (collectionEventInfo.getEventType() == eventType) {
                    listener.onEvent(collectionEventInfo);
                }
            }
        };
        return subscribe(filterListener);
    }

    public void unsubscribe(EntityChangeListener<T> listener) {
        Nitrite db = databaseService.getNitriteDB();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.unsubscribe(listener);
    }

    @SuppressWarnings("unchecked")
    public Class<T> getGenericSuperclass() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) superclass.getActualTypeArguments()[0];
    }

    private void validate(T entity) {
        Validator validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();

        if (entity == null) {
            throw new IllegalArgumentException("Entity is required");
        }

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        ArrayList<String> contraintMessages = new ArrayList<>();

        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            String constraintMessage = constraintViolation.getPropertyPath() + " -> "
                    + constraintViolation.getMessage();
            contraintMessages.add(constraintMessage);
        }

        if (!constraintViolations.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", contraintMessages));
        }
    }
}
