package ai.dragon.model;

import java.util.UUID;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(value = "silo")
@Getter
@Setter
public class SiloEntity implements IAbstractModel {
    @Id
    private UUID uuid;
    private String name;

    public SiloEntity() {
        this.uuid = UUID.randomUUID();
    }
}
