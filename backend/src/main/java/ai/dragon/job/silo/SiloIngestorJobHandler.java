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
import ai.dragon.job.silo.ingestor.AbstractSiloIngestor;
import ai.dragon.job.silo.ingestor.LocalSiloIngestor;
import ai.dragon.repository.SiloRepository;
import dev.langchain4j.data.document.Document;
import jakarta.activation.UnsupportedDataTypeException;

@Component
public class SiloIngestorJobHandler implements JobRequestHandler<SiloIngestorJobRequest> {
    public static final String JOB_NAME = "Silo Ingestor Job";

    @Autowired
    private SiloRepository siloRepository;

    @Override
    @Job(name = JOB_NAME, retries = 10, labels = { "silo", "ingestor" })
    public void run(SiloIngestorJobRequest jobRequest) throws Exception {
        jobContext().logger().info(String.format("Silo Ingestor Job : %s", jobRequest.getUuid()));
        JobDashboardProgressBar progressBar = jobContext().progressBar(100);
        SiloEntity siloEntity = siloRepository.getByUuid(jobRequest.getUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
        launchIngestDocumentsToSilo(siloEntity, progressBar);
    }

    private void launchIngestDocumentsToSilo(SiloEntity siloEntity, JobDashboardProgressBar progressBar)
            throws Exception {
        AbstractSiloIngestor ingestor = getIngestorFromEntity(siloEntity);
        List<Document> documents = listDocumentsFromIngestor(ingestor);
        ingestDocumentsToSilo(documents, progressBar);
    }

    private void ingestDocumentsToSilo(List<Document> documents, JobDashboardProgressBar progressBar)
            throws Exception {
        jobContext().logger().info(String.format("Ingesting %d documents to Silo...", documents.size()));
        for (int i = 0; i < documents.size(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            int progress = (i * 100) / documents.size();
            Document document = documents.get(i);
            jobContext().logger().info(document.metadata().toString());
            progressBar.setProgress(progress);
        }
        jobContext().logger().info("End.");
        progressBar.setProgress(100);
    }

    private List<Document> listDocumentsFromIngestor(AbstractSiloIngestor ingestor) throws Exception {
        jobContext().logger().info("Listing documents...");
        return ingestor.listDocuments();
    }

    private AbstractSiloIngestor getIngestorFromEntity(SiloEntity siloEntity) throws Exception {
        switch (siloEntity.getIngestorType()) {
            case LOCAL:
                return new LocalSiloIngestor(siloEntity);
            default:
                throw new UnsupportedDataTypeException("Ingestor type not supported");
        }
    }
}
