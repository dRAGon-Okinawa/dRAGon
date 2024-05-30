package ai.dragon.job.silo;

import java.util.UUID;
import org.jobrunr.jobs.lambdas.JobRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
public class SiloIngestorRequest implements JobRequest {
    private UUID uuid;

    @Override
    public Class<SiloIngestorJobHandler> getJobRequestHandler() {
        return SiloIngestorJobHandler.class;
    }

    public static SiloIngestorRequest create() {
        return new SiloIngestorRequest();
    }
}
