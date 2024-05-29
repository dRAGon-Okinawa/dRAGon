package ai.dragon.service;

import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.dizitart.no2.collection.events.EventType;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.SiloJobRequest;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.repository.SiloRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class SiloJobService {
    private final Logger logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));

    @Autowired
    private SiloRepository siloRepository;

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
            logger.info(String.format("Creating Recurrent Job for Silo : %s", siloEntity.getUuid().toString()));
            RecurringJobBuilder jobBuilder = RecurringJobBuilder.aRecurringJob()
                    .withId(siloEntity.getUuid().toString())
                    .withName("Silo Recurrent Job")
                    .withCron("* * * * *")
                    .withJobRequest(new SiloJobRequest()); // Passer UUID !
            BackgroundJob.createRecurrently(jobBuilder);
        });
    }

    @PreDestroy
    private void destroy() {
        siloRepository.unsubscribe(entityChangeListener);
    }
}
