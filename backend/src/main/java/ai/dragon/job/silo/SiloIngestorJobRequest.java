package ai.dragon.job.silo;

import java.util.UUID;
import org.jobrunr.jobs.lambdas.JobRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
public class SiloIngestorJobRequest implements JobRequest {
    private UUID uuid;

    @Override
    public Class<SiloIngestorJobHandler> getJobRequestHandler() {
        return SiloIngestorJobHandler.class;
    }

    public static SiloIngestorJobRequest create() {
        return new SiloIngestorJobRequest();
    }
}
