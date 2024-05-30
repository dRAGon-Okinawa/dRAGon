package ai.dragon.job;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.dragon.repository.FarmRepository;

@Component
public class SiloIngestorJobHandler implements JobRequestHandler<SiloIngestorRequest> {
    @Autowired
    private FarmRepository siloRepository;

    @Override
    @Job(name = "Silo Ingestor Job", retries = 10)
    public void run(SiloIngestorRequest jobRequest) {
        JobDashboardProgressBar progressBar = jobContext().progressBar(100);
        progressBar.setProgress(100);
        jobContext().logger().info("Job is running");
    }
}
