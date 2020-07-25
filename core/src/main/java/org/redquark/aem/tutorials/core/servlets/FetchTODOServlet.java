package org.redquark.aem.tutorials.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.servlet.Servlet;
import java.util.Objects;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.resourceTypes=" + "aemtutorials/components/content/todo"
        }
)
public class FetchTODOServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 7762806638577908286L;
    private static final String TAG = FetchTODOServlet.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTODOServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            // Getting the ResourceResolver from the current request
            ResourceResolver resourceResolver = request.getResourceResolver();
            // Getting the session instance by adapting ResourceResolver
            Session session = resourceResolver.adaptTo(Session.class);
            // Get the instance of QueryManager from the JCR workspace
            QueryManager queryManager = Objects.requireNonNull(session).getWorkspace().getQueryManager();
            // This query will look for all the assets under the given path
            String queryString = "SELECT * FROM [nt:unstructured] WHERE ISDESCENDANTNODE('/aemtutorials/data/todo')";
            // Converting the String query into an executable query object
            Query query = queryManager.createQuery(queryString, "JCR-SQL2");
            // Executing the query
            QueryResult queryResult = query.execute();
            // This will behave as a cursor pointing to the current row of results
            RowIterator rowIterator = queryResult.getRows();
            // Output
            StringBuilder output = new StringBuilder();
            // Add heading of the results
            output.append("<h2>").append("TODO List:").append("</h2>");
            // Loop for all the rows in the result and return them as json
            while (rowIterator.hasNext()) {
                Row row = rowIterator.nextRow();
                output.append("<p>").append("ID: ")
                        .append(row.getNode().getProperty("id").getLong())
                        .append(", ")
                        .append("Title: ").append(row.getNode().getProperty("title").getString())
                        .append("</p>");
            }
            // Printing the response to the browser window
            response.getWriter().println(output.toString());
        } catch (Exception e) {
            LOGGER.error("{}: Exception occurred: {}", TAG, e.getMessage());
        }
    }
}
