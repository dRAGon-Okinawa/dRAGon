package ai.dragon.service;

import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.dizitart.no2.collection.events.EventType;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.SiloIngestorRequest;
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

    private EntityChangeListener<SiloEntity> entityChangeListener;

    @PostConstruct
    private void init() {
        // Subscribe to SiloEntity changes
        entityChangeListener = siloRepository.subscribe(new EntityChangeListener<SiloEntity>() {
            @Override
            public void onChangeEvent(CollectionEventInfo<?> collectionEventInfo, UUID uuid) {
                if (collectionEventInfo.getEventType() == EventType.Insert) {
                    logger.info(uuid.toString());
                }
            }
        });

        // Create Recurrent Job for each Silo
        siloRepository.find().forEach(siloEntity -> {
            logger.info(String.format("Creating Silo Ingestor Job : %s", siloEntity.getUuid().toString()));
            SiloIngestorRequest jobRequest = SiloIngestorRequest
                    .create()
                    .uuid(siloEntity.getUuid());
            jobRequestScheduler.scheduleRecurrently(
                    siloEntity.getUuid().toString(),
                    "* * * * * ", // TODO Move to SiloEntity settings
                    java.time.ZoneId.of("UTC"), // TODO Move to configuration
                    jobRequest);
        });
    }

    @PreDestroy
    private void destroy() {
        siloRepository.unsubscribe(entityChangeListener);

        // TODO Stop all Recurrent Jobs
    }
}
