package org.redquark.aem.tutorials.core.schedulers;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.redquark.aem.tutorials.core.configs.WorkflowStatusConfiguration;
import org.redquark.aem.tutorials.core.services.EmailService;
import org.redquark.aem.tutorials.core.services.ResourceResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.time.LocalDateTime;

import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.NEW_LINE;
import static org.redquark.aem.tutorials.core.schedulers.WorkflowStatusScheduler.NAME;

@Component(
        immediate = true,
        service = Runnable.class,
        property = {
                Constants.SERVICE_ID + EQUALS + NAME
        }
)
@Designate(ocd = WorkflowStatusConfiguration.class)
public class WorkflowStatusScheduler implements Runnable {

    protected static final String NAME = "Workflow Status Scheduler";

    private static final String TAG = WorkflowStatusScheduler.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowStatusScheduler.class);
    @Reference
    Scheduler scheduler;
    @Reference
    WorkflowService workflowService;
    @Reference
    ResourceResolverService resourceResolverService;
    @Reference
    EmailService emailService;
    private String schedulerName;
    private String toEmail;
    private String ccEmail;
    private String fromEmail;
    private String subject;

    @Activate
    protected void activate(WorkflowStatusConfiguration configuration) {
        LOGGER.debug("{}: initializing properties for scheduler", TAG);
        this.schedulerName = configuration.schedulerName();
        LOGGER.debug("{}: name of the scheduler: {}", TAG, schedulerName);
        // Details for email
        this.toEmail = configuration.toEmail();
        this.ccEmail = configuration.ccEmail();
        this.fromEmail = configuration.fromEmail();
        this.subject = configuration.subject();
    }

    @Modified
    protected void modified(WorkflowStatusConfiguration configuration) {
        LOGGER.info("{}: modifying scheduler with name: {}", TAG, schedulerName);
        // Remove the scheduler registered with old configuration
        removeScheduler(configuration);
        // Add the scheduler registered with new configuration
        addScheduler(configuration);
    }

    @Deactivate
    protected void deactivate(WorkflowStatusConfiguration configuration) {
        LOGGER.debug("{}: removing scheduler: {}", TAG, schedulerName);
        removeScheduler(configuration);
    }

    private void addScheduler(WorkflowStatusConfiguration configuration) {
        // Check if the scheduler has enable flag set to true
        if (configuration.enabled()) {
            LOGGER.info("{}: scheduler: {} is enabled", TAG, schedulerName);
            // Configure the scheduler to use cron expression and some other properties
            ScheduleOptions scheduleOptions = scheduler.EXPR(configuration.cronExpression());
            scheduleOptions.name(schedulerName);
            scheduleOptions.canRunConcurrently(false);
            // Scheduling the job
            scheduler.schedule(this, scheduleOptions);
            LOGGER.info("{}: scheduler {} is added", TAG, schedulerName);
        } else {
            LOGGER.info("{}: scheduler {} is disabled", TAG, schedulerName);
            removeScheduler(configuration);
        }
    }

    private void removeScheduler(WorkflowStatusConfiguration configuration) {
        LOGGER.info("{}: removing scheduler {}", TAG, schedulerName);
        scheduler.unschedule(configuration.schedulerName());
    }

    private String getWorkflowStatus() {
        // This string will store the status for all workflows and other data
        StringBuilder workflowDetails = new StringBuilder();
        try {
            // Get the JCR session
            Session session = resourceResolverService.getResourceResolver().adaptTo(Session.class);
            // Get the workflow session
            WorkflowSession workflowSession = workflowService.getWorkflowSession(session);
            // States by which we want to query the workflows
            String[] states = {"RUNNING", "COMPLETED"};
            // Get the list of all the workflows by states
            Workflow[] workflows = workflowSession.getWorkflows(states);
            // Loop through all the workflows
            for (Workflow workflow : workflows) {
                workflowDetails
                        .append("ID: ")
                        .append(workflow.getId())
                        .append(NEW_LINE)
                        .append("Payload: ")
                        .append(workflow.getWorkflowData().getPayload())
                        .append(NEW_LINE)
                        .append("State: ")
                        .append(workflow.getState())
                        .append(NEW_LINE);
            }
        } catch (WorkflowException e) {
            LOGGER.error("{}: exception occurred: {}", TAG, e.getMessage());
        }
        return workflowDetails.toString();
    }

    @Override
    public void run() {
        // Getting the workflow status
        String workflowStatus = getWorkflowStatus();
        // Make the content ready
        String content = "Hi, " + NEW_LINE + "Following is the workflow status at: " + LocalDateTime.now() + NEW_LINE + workflowStatus;
        // Send emails
        emailService.sendEmail(toEmail, ccEmail, fromEmail, subject, content);
        LOGGER.info("{}: workflow status email is sent", TAG);
    }
}
