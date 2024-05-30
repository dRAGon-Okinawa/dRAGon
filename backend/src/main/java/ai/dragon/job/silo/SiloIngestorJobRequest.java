package ai.dragon.job.silo;

import java.util.UUID;

import org.jobrunr.jobs.lambdas.JobRequest;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class SiloIngestorJobRequest implements JobRequest {
    private UUID uuid;

    @Override
    public Class<SiloIngestorJobHandler> getJobRequestHandler() {
        return SiloIngestorJobHandler.class;
    }
}
