package ai.dragon.job.silo.ingestor.dto.loader;

import ai.dragon.enumeration.SiloIngestProgressMessageLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiloIngestLoaderLogMessage {
    private String message;

    @Builder.Default
    private SiloIngestProgressMessageLevel messageLevel = SiloIngestProgressMessageLevel.Information;
}

