package ai.dragon.service;

import java.util.UUID;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.dizitart.no2.collection.events.EventType;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.IngestorEntity;
import ai.dragon.listener.EntityChangeListener;
import ai.dragon.repository.IngestorRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class IngestorJobService {
    private final Logger logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));

    public static final String INGESTOR_RECURRING_JOB_ID = "ingestor-recurring-job";

    @Autowired
    private IngestorRepository ingestorRepository;

    @Autowired
    private JobScheduler jobScheduler;

    private EntityChangeListener<IngestorEntity> entityChangeListener;

    @PostConstruct
    private void init() {
        logger.error("IngestorJobService PostConstruct");

        entityChangeListener = ingestorRepository.subscribe(new EntityChangeListener<IngestorEntity>() {
            @Override
            public void onChangeEvent(CollectionEventInfo<?> collectionEventInfo, UUID uuid) {
                if (collectionEventInfo.getEventType() == EventType.Insert) {
                    logger.info(uuid.toString());
                }
            }
        });
    }

    @PreDestroy
    private void destroy() {
        ingestorRepository.unsubscribe(entityChangeListener);
    }

    @Recurring(id = INGESTOR_RECURRING_JOB_ID, cron = "* * * * *")
    @Job(name = "Ingestor Recurring Job")
    public void executeSampleJob(JobContext jobContext) {
        logger.info("testinfo");
        jobContext.logger().info("new info");
    }
}
