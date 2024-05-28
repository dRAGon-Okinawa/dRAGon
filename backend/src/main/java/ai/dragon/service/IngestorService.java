package ai.dragon.service;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IngestorService {
    private final Logger logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));

    public static final String INGESTOR_RECURRING_JOB_ID = "ingestor-recurring-job";

    @Recurring(id = INGESTOR_RECURRING_JOB_ID, cron = "* * * * *")
    @Job(name = "Ingestor Recurring Job")
    public void executeSampleJob(JobContext jobContext) {
        logger.info("testinfo");
        jobContext.logger().info("new info");
    }
}
