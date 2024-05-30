package ai.dragon.service;

import java.util.Optional;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.jobrunr.jobs.RecurringJob;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.SiloIngestorJobHandler;
import ai.dragon.job.silo.SiloIngestorJobRequest;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.repository.SiloRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class SiloJobService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SiloRepository siloRepository;

    @Autowired
    private JobRequestScheduler jobRequestScheduler;

    @Autowired
    private JobService jobService;

    private EntityChangeListener<SiloEntity> entityChangeListener;

    @PostConstruct
    private void init() {
        // Subscribe to SiloEntity changes
        entityChangeListener = siloRepository.subscribe(new EntityChangeListener<SiloEntity>() {
            @Override
            public void onChangeEvent(CollectionEventInfo<?> collectionEventInfo, SiloEntity entity) {
                switch (collectionEventInfo.getEventType()) {
                    case Insert:
                        createSiloIngestorJob(entity);
                        break;
                    case Remove:
                        removeSiloIngestorJob(entity);
                        break;
                    case Update:
                        restartSiloIngestorJob(entity);
                        break;
                    default:
                        break;
                }
            }
        });

        // Create Recurrent Job for each Silo
        siloRepository.find().forEach(siloEntity -> {
            createSiloIngestorJob(siloEntity);
        });
    }

    @PreDestroy
    private void destroy() {
        siloRepository.unsubscribe(entityChangeListener);
        jobService.removeAllRecurringJobs();
    }

    public void restartSiloIngestorJob(SiloEntity siloEntity) {
        logger.info(
                String.format("Restarting Silo Ingestor Job : %s -> %s", siloEntity.getUuid(), siloEntity.getName()));
        removeSiloIngestorJob(siloEntity);
        createSiloIngestorJob(siloEntity);
    }

    public void removeSiloIngestorJob(SiloEntity siloEntity) {
        logger.info(String.format("Removing Silo Ingestor Job : %s -> %s", siloEntity.getUuid(), siloEntity.getName()));
        jobService.removeRecurringJob(siloEntity.getUuid().toString());
    }

    public void createSiloIngestorJob(SiloEntity siloEntity) {
        logger.info(String.format("Creating Silo Ingestor Job : %s -> %s", siloEntity.getUuid(), siloEntity.getName()));
        try {
            SiloIngestorJobRequest jobRequest = SiloIngestorJobRequest
                    .builder()
                    .uuid(siloEntity.getUuid())
                    .build();
            jobRequestScheduler.scheduleRecurrently(
                    siloEntity.getUuid().toString(),
                    Optional.ofNullable(siloEntity.getIngestorSchedule()).orElse(SiloEntity.DEFAULT_CRON_EXPRESSION),
                    java.time.ZoneId.of("UTC"), // TODO Move to configuration
                    jobRequest);
            RecurringJob job = jobService.getRecurringJob(siloEntity.getUuid().toString());
            job.setJobName(String.format("%s : %s", SiloIngestorJobHandler.JOB_NAME, siloEntity.getName()));
        } catch (Exception ex) {
            logger.error("Error creating Silo Ingestor Job", ex);
        }
    }
}
