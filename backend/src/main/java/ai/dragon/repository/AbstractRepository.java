package ai.dragon.repository;

import java.util.UUID;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.WriteResult;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.dragon.model.IAbstractModel;
import ai.dragon.model.SiloEntity;
import ai.dragon.service.DatabaseService;

@Component
abstract class AbstractRepository<T extends IAbstractModel> {
    @Autowired
    private DatabaseService databaseService;

    public void save(IAbstractModel entity) {
        if(entity.getUuid() == null) {
            throw new IllegalArgumentException("UUID is required");
        }

        Nitrite db = databaseService.getDb();
        ObjectRepository<IAbstractModel> repository = db.getRepository(IAbstractModel.class);
        WriteResult result = exists(entity.getUuid()) ? repository.update(entity) : repository.insert(entity);
    }

    public boolean exists(UUID uuid) {
       return getByUuid(uuid) != null;
    }

    public T getByUuid(UUID uuid) {
        Nitrite db = databaseService.getDb();
        ObjectRepository<IAbstractModel> repository = db.getRepository(IAbstractModel.class);
        return repository.getById(uuid);
    }

    public Cursor<T> find() {
        Nitrite db = databaseService.getDb();
        ObjectRepository<IAbstractModel> repository = db.getRepository(IAbstractModel.class);
        return repository.find();
    }
}
