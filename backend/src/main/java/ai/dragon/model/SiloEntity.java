package ai.dragon.model;

import java.util.UUID;

import org.dizitart.no2.repository.annotations.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity(value = "silo")
@Getter
@Setter
public class SiloEntity implements IAbstractModel {
    private UUID uuid;
    private String name;
}
