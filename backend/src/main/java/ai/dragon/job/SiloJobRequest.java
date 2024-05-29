package ai.dragon.job;

import org.jobrunr.jobs.lambdas.JobRequest;

public class SiloJobRequest implements JobRequest {
    @Override
    public Class<SiloJobHandler> getJobRequestHandler() {
        return SiloJobHandler.class;
    }
}
