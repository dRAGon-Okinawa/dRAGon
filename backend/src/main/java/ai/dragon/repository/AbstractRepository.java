package ai.dragon.repository;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.dizitart.no2.collection.events.EventType;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.dizitart.no2.transaction.Session;
import org.dizitart.no2.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.AbstractEntity;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.service.DatabaseService;
import ai.dragon.util.ThrowingFunction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Component
public abstract class AbstractRepository<T extends AbstractEntity> {
    @Autowired
    protected DatabaseService databaseService;

    private ObjectRepository<T> objectRepository;

    public void executeTransaction(Consumer<AbstractRepository<T>> transactionConsumer) {
        if (this instanceof TransactionalRepository) {
            throw new IllegalStateException("Nested transactions are not allowed");
        }
        Nitrite db = databaseService.getNitriteDB();
        try (Session session = db.createSession()) {
            try (Transaction transaction = session.beginTransaction()) {
                ObjectRepository<T> transactionRepository = transaction.getRepository(getGenericSuperclass());
                AbstractRepository<T> transactionalRepository = new TransactionalRepository<>(transactionRepository,
                        getGenericSuperclass(), databaseService);
                try {
                    transactionConsumer.accept(transactionalRepository);
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                }
            }
        }
    }

    public <R> R queryTransaction(ThrowingFunction<AbstractRepository<T>, R> transactionFunction) throws Exception {
        if (this instanceof TransactionalRepository) {
            throw new IllegalStateException("Nested transactions are not allowed");
        }
        Nitrite db = databaseService.getNitriteDB();
        try (Session session = db.createSession()) {
            try (Transaction transaction = session.beginTransaction()) {
                ObjectRepository<T> transactionRepository = transaction.getRepository(getGenericSuperclass());
                AbstractRepository<T> transactionalRepository = new TransactionalRepository<>(transactionRepository,
                        getGenericSuperclass(), databaseService);
                R result = null;
                try {
                    result = transactionFunction.apply(transactionalRepository);
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                }
                return result;
            }
        }
    }

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

        ObjectRepository<T> repository = getObjectRepository();

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
        ObjectRepository<T> repository = getObjectRepository();
        return Optional.ofNullable(repository.getById(uuid));
    }

    public Cursor<T> find() {
        ObjectRepository<T> repository = getObjectRepository();
        return repository.find();
    }

    public Cursor<T> findWithFilter(Filter filter) {
        return findWithFilter(filter, null);
    }

    public Cursor<T> findWithFilter(Filter filter, FindOptions findOptions) {
        ObjectRepository<T> repository = getObjectRepository();
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
        ObjectRepository<T> repository = getObjectRepository();
        repository.remove(entity);
    }

    public void deleteAll() {
        ObjectRepository<T> repository = getObjectRepository();
        repository.clear();
    }

    public long countAll() {
        ObjectRepository<T> repository = getObjectRepository();
        return repository.size();
    }

    public EntityChangeListener<T> subscribe(EntityChangeListener<T> listener) {
        ObjectRepository<T> repository = getObjectRepository();
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
        ObjectRepository<T> repository = getObjectRepository();
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

    protected ObjectRepository<T> getObjectRepository() {
        if (objectRepository != null) {
            return objectRepository;
        }

        Nitrite db = databaseService.getNitriteDB();
        objectRepository = db.getRepository(getGenericSuperclass());

        return objectRepository;
    }

    // Inner class to handle transactional operations
    private static class TransactionalRepository<T extends AbstractEntity> extends AbstractRepository<T> {
        private final ObjectRepository<T> objectRepository;
        private final Class<T> type;

        TransactionalRepository(ObjectRepository<T> objectRepository, Class<T> type, DatabaseService databaseService) {
            super();
            this.objectRepository = objectRepository;
            this.databaseService = databaseService;
            this.type = type;
        }

        @Override
        protected ObjectRepository<T> getObjectRepository() {
            return this.objectRepository;
        }

        @Override
        public Class<T> getGenericSuperclass() {
            return this.type;
        }
    }
}
