package ai.dragon.job.silo.ingestor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiloIngestProgress {
    private Integer progressPercentage;
    private String message;

    @Builder.Default
    private SiloIngestProgressMessageLevel messageLevel = SiloIngestProgressMessageLevel.Information;
}

enum SiloIngestProgressMessageLevel {
    Information,
    Warning,
    Error
}
