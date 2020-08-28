package org.redquark.aem.tutorials.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static org.redquark.aem.tutorials.core.configs.WorkflowStatusConfiguration.CONFIGURATION_DESCRIPTION;
import static org.redquark.aem.tutorials.core.configs.WorkflowStatusConfiguration.CONFIGURATION_NAME;

@ObjectClassDefinition(
        name = CONFIGURATION_NAME,
        description = CONFIGURATION_DESCRIPTION
)
public @interface WorkflowStatusConfiguration {

    String CONFIGURATION_NAME = "Workflow Status Configuration";
    String CONFIGURATION_DESCRIPTION = "This configuration captures the details for getting workflow status and sending email";
    String DEFAULT_EMAIL_ADDRESS = "anirudh03sharma@gmail.com";

    @AttributeDefinition(
            name = "Scheduler Name",
            description = "Enter a unique identifier that represents name of the scheduler",
            type = AttributeType.STRING
    )
    String schedulerName() default CONFIGURATION_NAME;

    @AttributeDefinition(
            name = "Enabled",
            description = "Check the box to enable the scheduler",
            type = AttributeType.BOOLEAN
    )
    boolean enabled() default false;

    @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression which will decide how the scheduler will run",
            type = AttributeType.STRING
    )
    String cronExpression() default "0 * * * * ?";

    @AttributeDefinition(
            name = "To Email",
            description = "Enter the email address of recipient in TO field",
            type = AttributeType.STRING
    )
    String toEmail() default DEFAULT_EMAIL_ADDRESS;

    @AttributeDefinition(
            name = "Cc Email",
            description = "Enter the email address of recipient in CC field",
            type = AttributeType.STRING
    )
    String ccEmail() default DEFAULT_EMAIL_ADDRESS;

    @AttributeDefinition(
            name = "From Email",
            description = "Enter the email addresses of the sender",
            type = AttributeType.STRING
    )
    String fromEmail() default DEFAULT_EMAIL_ADDRESS;

    @AttributeDefinition(
            name = "Subject",
            description = "Enter the subject of the email",
            type = AttributeType.STRING
    )
    String subject() default CONFIGURATION_NAME;
}
