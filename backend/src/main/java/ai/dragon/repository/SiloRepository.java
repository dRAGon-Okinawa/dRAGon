package ai.dragon.repository;

import java.util.UUID;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.WriteResult;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.dragon.model.SiloEntity;
import ai.dragon.service.DatabaseService;

@Component
public class SiloRepository {
    @Autowired
    private DatabaseService databaseService;

    public void save(SiloEntity silo) {
        if(silo.getUuid() == null) {
            throw new IllegalArgumentException("UUID is required");
        }

        Nitrite db = databaseService.getDb();
        ObjectRepository<SiloEntity> repository = db.getRepository(SiloEntity.class);
        WriteResult result = exists(silo.getUuid()) ? repository.update(silo) : repository.insert(silo);
    }

    public boolean exists(UUID uuid) {
       return getByUuid(uuid) != null;
    }

    public SiloEntity getByUuid(UUID uuid) {
        Nitrite db = databaseService.getDb();
        ObjectRepository<SiloEntity> repository = db.getRepository(SiloEntity.class);
        return repository.getById(uuid);
    }

    public Cursor<SiloEntity> find() {
        Nitrite db = databaseService.getDb();
        ObjectRepository<SiloEntity> repository = db.getRepository(SiloEntity.class);
        return repository.find();
    }
}
