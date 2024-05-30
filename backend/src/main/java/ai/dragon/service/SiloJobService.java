package ai.dragon.service;

import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
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
            public void onChangeEvent(CollectionEventInfo<?> collectionEventInfo, UUID uuid) {
                switch (collectionEventInfo.getEventType()) {
                    case Insert:
                        createSiloIngestorJob(uuid);
                        break;
                    case Remove:
                        removeSiloIngestorJob(uuid);
                        break;
                    case Update:
                        restartSiloIngestorJob(uuid);
                        break;
                    default:
                        break;
                }
            }
        });

        // Create Recurrent Job for each Silo
        siloRepository.find().forEach(siloEntity -> {
            createSiloIngestorJob(siloEntity.getUuid());
        });
    }

    @PreDestroy
    private void destroy() {
        siloRepository.unsubscribe(entityChangeListener);
        jobService.removeAllRecurringJobs();
    }

    public void restartSiloIngestorJob(UUID uuid) {
        logger.info(String.format("Restarting Silo Ingestor Job : %s", uuid.toString()));
        removeSiloIngestorJob(uuid);
        createSiloIngestorJob(uuid);
    }

    public void removeSiloIngestorJob(UUID uuid) {
        logger.info(String.format("Removing Silo Ingestor Job : %s", uuid.toString()));
        jobService.removeRecurringJob(uuid.toString());
    }

    public void createSiloIngestorJob(UUID uuid) {
        logger.info(String.format("Creating Silo Ingestor Job : %s", uuid.toString()));
        SiloIngestorJobRequest jobRequest = SiloIngestorJobRequest
                .create()
                .uuid(uuid);
        jobRequestScheduler.scheduleRecurrently(
                uuid.toString(),
                "* * * * * ", // TODO Move to SiloEntity settings
                java.time.ZoneId.of("UTC"), // TODO Move to configuration
                jobRequest);
    }
}
