package ai.dragon.job.silo;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;

@Component
public class SiloIngestorJobHandler implements JobRequestHandler<SiloIngestorJobRequest> {
    private final Logger logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));

    @Autowired
    private SiloRepository siloRepository;

    @Override
    @Job(name = "Silo Ingestor Job", retries = 10, labels = { "silo", "ingestor" })
    public void run(SiloIngestorJobRequest jobRequest) {
        JobDashboardProgressBar progressBar = jobContext().progressBar(100);
        jobContext().logger().info(String.format("Running Job for Silo : %s", jobRequest.uuid()));
        SiloEntity siloEntity = siloRepository.getByUuid(jobRequest.uuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
        ingestDataToSilo(siloEntity, progressBar);
    }

    private void ingestDataToSilo(SiloEntity siloEntity, JobDashboardProgressBar progressBar) {
        jobContext().logger().info(String.format("Name : %s", siloEntity.getName()));
        progressBar.setProgress(100);

        // TODO Loop => if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
    }
}
