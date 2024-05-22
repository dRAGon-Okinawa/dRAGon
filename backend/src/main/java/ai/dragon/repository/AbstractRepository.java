package ai.dragon.repository;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.events.CollectionEventListener;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.dragon.model.IAbstractModel;
import ai.dragon.service.DatabaseService;

@Component
abstract class AbstractRepository<T extends IAbstractModel> {
    @Autowired
    private DatabaseService databaseService;

    @SuppressWarnings("unchecked")
    public void save(T entity) {
        if (entity.getUuid() == null) {
            throw new IllegalArgumentException("UUID is required");
        }

        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());

        if (exists(entity.getUuid())) {
            repository.update(entity);
        } else {
            repository.insert(entity);
        }
    }

    public boolean exists(UUID uuid) {
        return getByUuid(uuid) != null;
    }

    public T getByUuid(UUID uuid) {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return repository.getById(uuid);
    }

    public Cursor<T> find() {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return repository.find();
    }

    public Cursor<T> findWithFilter(Filter filter) {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return repository.find(filter);
    }

    public Cursor<T> findByFieldValue(String fieldName, Object fieldValue) {
        return this.findWithFilter(FluentFilter.where(fieldName).eq(fieldValue));
    }

    public void delete(UUID uuid) {
        delete(getByUuid(uuid));
    }

    public void delete(T entity) {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.remove(entity);
    }

    public void deleteAll() {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.clear();
    }

    public long countAll() {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        return repository.size();
    }

    public void subscribe(CollectionEventListener listener) {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.subscribe(listener);
    }

    public void unsubscribe(CollectionEventListener listener) {
        Nitrite db = databaseService.getDb();
        ObjectRepository<T> repository = db.getRepository(getGenericSuperclass());
        repository.unsubscribe(listener);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getGenericSuperclass() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) superclass.getActualTypeArguments()[0];
    }
}
