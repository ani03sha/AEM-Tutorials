package org.redquark.aem.tutorials.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.redquark.aem.tutorials.core.services.ResourceResolverService;
import org.redquark.aem.tutorials.core.services.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.day.cq.wcm.api.NameConstants.NT_PAGE;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.services.impl.SearchServiceImpl.SERVICE_DESCRIPTION;
import static org.redquark.aem.tutorials.core.services.impl.SearchServiceImpl.SERVICE_NAME;

@Component(
        service = SearchService.class,
        property = {
                Constants.SERVICE_ID + EQUALS + SERVICE_NAME,
                Constants.SERVICE_DESCRIPTION + EQUALS + SERVICE_DESCRIPTION
        }
)
public class SearchServiceImpl implements SearchService {

    protected static final String SERVICE_NAME = "Search Service";
    protected static final String SERVICE_DESCRIPTION = "This services uses QueryBuilder API to search in JCR";

    private static final String TAG = SearchServiceImpl.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Reference
    QueryBuilder queryBuilder;

    @Reference
    ResourceResolverService resourceResolverService;

    @Override
    public List<String> searchByKeyword(String keyword) {
        LOGGER.debug("{}: trying to search for keyword: {}", TAG, keyword);
        // List of all the results
        List<String> resultPaths = new ArrayList<>();
        try {
            // Getting the instance of Resource Resolver
            ResourceResolver resourceResolver = resourceResolverService.getResourceResolver();
            // Adapting this resource resolver to get JCR session
            Session session = resourceResolver.adaptTo(Session.class);
            // Creating the predicates for the query using a map object
            Map<String, String> predicates = new HashMap<>();
            predicates.put("type", NT_PAGE);
            predicates.put("path", "/content/");
            predicates.put("fulltext", keyword);
            // Creating the query instance
            Query query = queryBuilder.createQuery(PredicateGroup.create(predicates), session);
            // Getting the results
            SearchResult searchResult = query.getResult();
            LOGGER.info("{}: number of results returned: {}", TAG, searchResult.getHits().size());
            // Loop through all the results
            for (Hit hit : searchResult.getHits()) {
                resultPaths.add(hit.getPath());
            }
        } catch (RepositoryException e) {
            LOGGER.error("{}: cannot search due to: {}", TAG, e.getMessage());
        }
        return resultPaths;
    }
}
