package org.redquark.aem.tutorials.core.workflows.participant;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.redquark.aem.tutorials.core.constants.AppConstants.ADMINISTRATORS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.CHILD_PAGE_COUNT;
import static org.redquark.aem.tutorials.core.constants.AppConstants.CHOOSER_LABEL;
import static org.redquark.aem.tutorials.core.constants.AppConstants.CONTENT_AUTHORS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.workflows.participant.ReviewChildrenPagesStep.CHOOSER_LABEL_VALUE;

@Component(
        service = ParticipantStepChooser.class,
        property = {
                CHOOSER_LABEL + EQUALS + CHOOSER_LABEL_VALUE
        }
)
public class ReviewChildrenPagesStep implements ParticipantStepChooser {

    protected static final String CHOOSER_LABEL_VALUE = "Review Children Pages";
    private static final String TAG = ReviewChildrenPagesStep.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewChildrenPagesStep.class);

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        // Get the count of the children pages in the workflow
        int childPagesCount = workItem.getWorkflow().getMetaDataMap().get(CHILD_PAGE_COUNT, 0);
        LOGGER.debug("{}: child pages count: {}", TAG, childPagesCount);
        // Return the user group based on the number of child pages
        return childPagesCount > 0 ? ADMINISTRATORS : CONTENT_AUTHORS;
    }
}
