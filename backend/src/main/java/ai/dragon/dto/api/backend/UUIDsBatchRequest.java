package ai.dragon.dto.api.backend;

import java.util.List;

import lombok.Data;

@Data
public class UUIDsBatchRequest {
    private List<String> uuids;
}
