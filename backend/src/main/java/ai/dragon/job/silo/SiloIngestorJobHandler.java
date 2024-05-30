package ai.dragon.job.silo;

import java.util.List;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.LocalSiloIngestor;
import ai.dragon.job.silo.ingestor.dto.IngestorDocument;
import ai.dragon.repository.SiloRepository;

@Component
public class SiloIngestorJobHandler implements JobRequestHandler<SiloIngestorJobRequest> {
    public static final String JOB_NAME = "Silo Ingestor Job";

    @Autowired
    private SiloRepository siloRepository;

    @Override
    @Job(name = JOB_NAME, retries = 10, labels = { "silo", "ingestor" })
    public void run(SiloIngestorJobRequest jobRequest) throws Exception {
        JobDashboardProgressBar progressBar = jobContext().progressBar(100);
        jobContext().logger().info(String.format("Silo Ingestor Job : %s", jobRequest.getUuid()));
        SiloEntity siloEntity = siloRepository.getByUuid(jobRequest.getUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
        ingestDataToSilo(siloEntity, progressBar);
    }

    private void ingestDataToSilo(SiloEntity siloEntity, JobDashboardProgressBar progressBar) throws Exception {
        // TODO LocalSiloIngestor => From enum
        LocalSiloIngestor ingestor = new LocalSiloIngestor(siloEntity);
        jobContext().logger().info("Listing documents...");
        List<IngestorDocument> documents = ingestor.listDocuments();
        jobContext().logger().info(String.format("Ingesting %d documents to Silo...", documents.size()));
        for (int i = 0; i < documents.size(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            int progress = (i * 100) / documents.size();
            IngestorDocument document = documents.get(i);
            jobContext().logger().info(document.getUri().toString());
            progressBar.setProgress(progress);
        }
        jobContext().logger().info("End.");
        progressBar.setProgress(100);
    }
}
