package org.barryjordan.propertytracker.scheduler;

import org.barryjordan.propertytracker.service.TrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

/**
 * Schedules tasks to run periodically.
 */
public class SchedulerInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTask.class);
    private final static long PERIOD_IN_MILLIS = 30000;

    /**
     * Creates a new instance.
     *
     * @param trackerService The services to execute.
     * @param taskScheduler The TaskScheduler in which to execute the services.
     */
    public SchedulerInit(final TrackerService trackerService, final TaskScheduler taskScheduler) {
        taskScheduler.scheduleAtFixedRate(new RequestTask(trackerService), PERIOD_IN_MILLIS);
    }

    /**
     *  Runnable to execute tracking task.
     */
    private static class RequestTask implements Runnable {

        private final TrackerService trackerService;

        /**
         * Creates a new instance.
         *
         * @param trackerService The tracking service to execute.
         */
        private RequestTask(final TrackerService trackerService) {
            this.trackerService = trackerService;
        }

        @Override
        public void run() {
            LOGGER.info("Task Running");
            trackerService.track();
        }
    }
}
