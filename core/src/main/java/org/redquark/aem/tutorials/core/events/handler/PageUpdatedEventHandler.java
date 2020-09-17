package org.redquark.aem.tutorials.core.events.handler;

import com.day.cq.wcm.api.PageEvent;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.PAGE_REPLICATION_TOPIC;

@Component(
        immediate = true,
        service = EventHandler.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= This event handler listens to the events on page modification",
                EventConstants.EVENT_TOPIC + EQUALS + PageEvent.EVENT_TOPIC
        }
)
public class PageUpdatedEventHandler implements EventHandler {

    private static final String TAG = PageUpdatedEventHandler.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(PageUpdatedEventHandler.class);

    @Reference
    JobManager jobManager;

    @Override
    @SuppressWarnings("unchecked")
    public void handleEvent(Event event) {
        LOGGER.debug("{}: event occurred for topic: {}", TAG, event.getTopic());
        try {
            // Get the payload path from the event
            List<HashMap<String, Object>> modifications = (List<HashMap<String, Object>>) event.getProperty("modifications");
            // Payload path
            String payload = (String) modifications.get(0).get("path");
            // Add stuff for the job
            Map<String, Object> jobProperties = new HashMap<>();
            jobProperties.put("payload", payload);
            // Add this job to the job manager
            jobManager.addJob(PAGE_REPLICATION_TOPIC, jobProperties);
            LOGGER.info("{}: job is completed successfully", TAG);
        } catch (Exception e) {
            LOGGER.error("{}: exception occurred: {}", TAG, e.getMessage());
        }
    }
}
