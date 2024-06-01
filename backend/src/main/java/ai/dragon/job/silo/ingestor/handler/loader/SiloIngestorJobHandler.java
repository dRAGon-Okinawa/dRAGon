package ai.dragon.job.silo.ingestor.handler.loader;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;
import ai.dragon.service.IngestorService;

@Component
public class SiloIngestorJobHandler implements JobRequestHandler<SiloIngestorJobRequest> {
    public static final String JOB_NAME = "Silo Ingestor Job";

    @Autowired
    private SiloRepository siloRepository;

    @Autowired
    private IngestorService ingestorService;

    @Override
    @Job(name = JOB_NAME, retries = 10, labels = { "silo", "ingestor" })
    public void run(SiloIngestorJobRequest jobRequest) throws Exception {
        jobContext().logger().info(String.format("Silo Ingestor Job : %s", jobRequest.getUuid()));
        JobDashboardProgressBar progressBar = jobContext().progressBar(100);
        SiloEntity siloEntity = siloRepository.getByUuid(jobRequest.getUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
        ingestorService.runSiloIngestion(siloEntity, ingestProgress -> {
            progressBar.setProgress(ingestProgress);
        }, ingestLogMessage -> {
            switch (ingestLogMessage.getMessageLevel()) {
                case Warning:
                    jobContext().logger().warn(ingestLogMessage.getMessage());
                    break;
                case Error:
                    jobContext().logger().error(ingestLogMessage.getMessage());
                    break;
                default:
                    jobContext().logger().info(ingestLogMessage.getMessage());
            }
        });
        jobContext().logger().info("End of Silo Ingestor Job.");
    }
}
