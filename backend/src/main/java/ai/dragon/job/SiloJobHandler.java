package ai.dragon.job;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.dragon.repository.SiloRepository;

@Component
public class SiloJobHandler implements JobRequestHandler<SiloJobRequest> {
    @Autowired
    private SiloRepository siloRepository;

    @Override
    @Job(name = "Some neat Job Display Name", retries = 10)
    public void run(SiloJobRequest jobRequest) {
        JobDashboardProgressBar progressBar = jobContext().progressBar(100);
        progressBar.setProgress(100);
        jobContext().logger().info("Job is running");
    }
}
