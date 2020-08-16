package org.redquark.aem.tutorials.core.workflows.participant;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.redquark.aem.tutorials.core.constants.AppConstants.ADMINISTRATORS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.CHOOSER_LABEL;
import static org.redquark.aem.tutorials.core.constants.AppConstants.CONTENT_AUTHORS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.workflows.participant.ApprovePageContentStep.CHOOSER_LABEL_VALUE;

@Component(
        service = ParticipantStepChooser.class,
        property = {
                CHOOSER_LABEL + EQUALS + CHOOSER_LABEL_VALUE
        }
)
public class ApprovePageContentStep implements ParticipantStepChooser {

    protected static final String CHOOSER_LABEL_VALUE = "Approve Page Content";
    private static final String TAG = ApprovePageContentStep.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovePageContentStep.class);
    private static final String CONTENT_PATH = "/content/aemtutorials";

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        // Getting payload from Workflow - workItem -> workflowData -> payload
        String payloadType = workItem.getWorkflowData().getPayloadType();
        LOGGER.debug("{}: Payload type: {}", TAG, payloadType);
        // Check type of payload; there are two - JCR_PATH and JCR_UUID
        if (StringUtils.equals(payloadType, "JCR_PATH")) {
            // Get the JCR path from the payload
            String path = workItem.getWorkflowData().getPayload().toString();
            LOGGER.debug("{}: Payload path: {}", TAG, path);
            // Get process arguments which will contain the properties to update
            if (path.startsWith(CONTENT_PATH)) {
                LOGGER.debug("{}: selected user/group: {}", TAG, CONTENT_AUTHORS);
                return CONTENT_AUTHORS;
            }
        }
        return ADMINISTRATORS;
    }
}
