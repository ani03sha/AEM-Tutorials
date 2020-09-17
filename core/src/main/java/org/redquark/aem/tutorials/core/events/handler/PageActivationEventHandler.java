package org.redquark.aem.tutorials.core.events.handler;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.replication.ReplicationAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.redquark.aem.tutorials.core.services.ResourceResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;

@Component(
        immediate = true,
        service = EventHandler.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= This event handler listens to the events on page activation",
                EventConstants.EVENT_TOPIC + EQUALS + ReplicationAction.EVENT_TOPIC
        }
)
public class PageActivationEventHandler implements EventHandler {

    private static final String TAG = PageActivationEventHandler.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(PageActivationEventHandler.class);

    @Reference
    ResourceResolverService resourceResolverService;

    @Override
    public void handleEvent(Event event) {
        LOGGER.debug("{}: event is registered: {}", TAG, event.getTopic());
        try {
            // Get the payload path from the request
            String[] paths = (String[]) event.getProperty("paths");
            // Get the payload path as the first page
            String payloadPath = paths[0];
            if (!StringUtils.isEmpty(payloadPath)) {
                // Getting the resource resolver
                final ResourceResolver resolver = resourceResolverService.getResourceResolver();
                // Get the workflow session from the resource resolver
                final WorkflowSession workflowSession = resolver.adaptTo(WorkflowSession.class);
                // Workflow model path - This is the already created workflow
                final String model = "/var/workflow/models/aemtutorials/update_referenced_assets";
                // Get the workflow model object
                final WorkflowModel workflowModel = Objects.requireNonNull(workflowSession).getModel(model);
                // Create a workflow Data (or Payload) object pointing to a resource via JCR
                // Path (alternatively, a JCR_UUID can be used)
                final WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payloadPath);
                // Start the workflow!
                workflowSession.startWorkflow(workflowModel, workflowData);
                LOGGER.info("Workflow: {} started", model);
            } else {
                LOGGER.error("{}: Payload path is not valid", TAG);
            }
        } catch (WorkflowException e) {
            LOGGER.error("{}: exception occurred: {}", TAG, e.getMessage());
        }
    }
}
