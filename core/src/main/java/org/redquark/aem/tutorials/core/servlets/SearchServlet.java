package org.redquark.aem.tutorials.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.redquark.aem.tutorials.core.services.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.NEW_LINE;
import static org.redquark.aem.tutorials.core.servlets.SearchServlet.PATH;
import static org.redquark.aem.tutorials.core.servlets.SearchServlet.SERVICE_NAME;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_ID + EQUALS + SERVICE_NAME,
                SLING_SERVLET_PATHS + EQUALS + PATH,
                SLING_SERVLET_METHODS + EQUALS + HttpConstants.METHOD_POST
        }
)
public class SearchServlet extends SlingAllMethodsServlet {

    protected static final String PATH = "/bin/aemtutorials/search";
    protected static final String SERVICE_NAME = "Search Servlet";

    private static final String TAG = SearchServlet.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServlet.class);

    @Reference
    SearchService searchService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            String keyword = request.getParameter("keyword");
            LOGGER.info("{}: searching for keyword: {}", TAG, keyword);
            // Getting the result from search service
            List<String> resultList = searchService.searchByKeyword(keyword);
            // Format the results
            StringBuilder formattedResult = new StringBuilder();
            for (String s : resultList) {
                formattedResult.append(s).append(NEW_LINE);
            }
            // Print the results on the screen
            response.getWriter().println(formattedResult.toString());
        } catch (IOException e) {
            LOGGER.error("{}: cannot search due to: {}", TAG, e.getMessage());
        }
    }
}
