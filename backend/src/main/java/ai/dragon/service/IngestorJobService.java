package ai.dragon.service;

import org.dizitart.no2.collection.events.CollectionEventInfo;
import org.dizitart.no2.collection.events.CollectionEventListener;
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

import ai.dragon.entity.IAbstractEntity;
import ai.dragon.entity.IngestorEntity;
import ai.dragon.repository.IngestorRepository;

import static org.jobrunr.scheduling.RecurringJobBuilder.aRecurringJob;

@Service
public class IngestorJobService {
    private final Logger logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));

    public static final String INGESTOR_RECURRING_JOB_ID = "ingestor-recurring-job";

    @Autowired
    private IngestorRepository ingestorRepository;

    @Autowired
    private JobScheduler jobScheduler;

    public void onApplicationStartup() {
        ingestorRepository.subscribe(new CollectionEventListener() {
            @Override
            public void onEvent(CollectionEventInfo<IngestorEntity> collectionEventInfo) {
                collectionEventInfo.getItem();
                if (EventType.Insert.equals(collectionEventInfo.getEventType())) {
                    UUID uuid = collectionEventInfo.getItem().getUuid();
                    jobScheduler.createRecurrently(aRecurringJob().withId);
                }
            }
        });
    }

    @Recurring(id = INGESTOR_RECURRING_JOB_ID, cron = "* * * * *")
    @Job(name = "Ingestor Recurring Job")
    public void executeSampleJob(JobContext jobContext) {
        logger.info("testinfo");
        jobContext.logger().info("new info");
    }
}
