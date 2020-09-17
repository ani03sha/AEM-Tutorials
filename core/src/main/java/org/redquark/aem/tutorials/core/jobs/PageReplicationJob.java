package org.redquark.aem.tutorials.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.redquark.aem.tutorials.core.services.ReplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.sling.event.jobs.consumer.JobConsumer.PROPERTY_TOPICS;
import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.service.event.EventConstants.SERVICE_ID;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.PAGE_REPLICATION_TOPIC;
import static org.redquark.aem.tutorials.core.jobs.PageReplicationJob.SERVICE_NAME;

@Component(
        immediate = true,
        service = JobConsumer.class,
        property = {
                SERVICE_ID + EQUALS + SERVICE_NAME,
                SERVICE_DESCRIPTION + EQUALS + "This job replicates the given payload",
                PROPERTY_TOPICS + EQUALS + PAGE_REPLICATION_TOPIC
        }
)
public class PageReplicationJob implements JobConsumer {

    protected static final String SERVICE_NAME = "Page Replication Job";
    private static final String TAG = PageReplicationJob.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(PageReplicationJob.class);

    @Reference
    ReplicationService replicationService;

    @Override
    public JobResult process(Job job) {
        LOGGER.debug("{}: trying to execute job: {}", TAG, job.getTopic());
        try {
            // Get the payload path from the job properties
            String payloadPath = (String) job.getProperty("payload");
            // Call the replication service
            replicationService.replicateContent(payloadPath);
            return JobResult.OK;
        } catch (Exception e) {
            LOGGER.error("{}: job failed due to: {}", TAG, e.getMessage());
            return JobResult.FAILED;
        }
    }
}
