package ai.dragon.job;

import org.jobrunr.jobs.lambdas.JobRequest;

public class SiloIngestorRequest implements JobRequest {
    @Override
    public Class<SiloIngestorJobHandler> getJobRequestHandler() {
        return SiloIngestorJobHandler.class;
    }
}
