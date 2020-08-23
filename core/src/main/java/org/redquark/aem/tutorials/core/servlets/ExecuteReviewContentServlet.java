package org.redquark.aem.tutorials.core.servlets;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Objects;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.servlets.ExecuteReviewContentServlet.PATHS;

@Component(
        service = Servlet.class,
        property = {
                SLING_SERVLET_METHODS + EQUALS + HttpConstants.METHOD_GET,
                SLING_SERVLET_PATHS + EQUALS + PATHS
        }
)
public class ExecuteReviewContentServlet extends SlingSafeMethodsServlet {

    protected static final String PATHS = "/bin/aemtutorials/executeWorkflow";
    private static final long serialVersionUID = 4235730140092282985L;
    private static final String TAG = ExecuteReviewContentServlet.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteReviewContentServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            // Get the payload path from the request
            String payloadPath = request.getParameter("path");
            if (!StringUtils.isEmpty(payloadPath)) {
                // Getting the resource resolver
                final ResourceResolver resolver = request.getResourceResolver();
                // Get the workflow session from the resource resolver
                final WorkflowSession workflowSession = resolver.adaptTo(WorkflowSession.class);
                // Workflow model path - This is the already created workflow
                final String model = "/var/workflow/models/aemtutorials/review-content";
                // Get the workflow model object
                final WorkflowModel workflowModel = Objects.requireNonNull(workflowSession).getModel(model);
                // Create a workflow Data (or Payload) object pointing to a resource via JCR
                // Path (alternatively, a JCR_UUID can be used)
                final WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payloadPath);
                // Start the workflow!
                workflowSession.startWorkflow(workflowModel, workflowData);
                LOGGER.info("Workflow: {} started", model);
                response.getWriter().println("Workflow Executed");
            } else {
                response.getWriter().println("Payload path is not present in the query parameter");
            }
        } catch (IOException | WorkflowException e) {
            LOGGER.error("{}: exception occurred: {}", TAG, e.getMessage());
        }
    }
}
