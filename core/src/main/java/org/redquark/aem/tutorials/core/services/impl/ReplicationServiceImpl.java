package org.redquark.aem.tutorials.core.services.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.redquark.aem.tutorials.core.services.ReplicationService;
import org.redquark.aem.tutorials.core.services.ResourceResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.day.cq.dam.api.DamConstants.MOUNTPOINT_ASSETS;
import static org.osgi.service.event.EventConstants.SERVICE_ID;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.services.impl.ReplicationServiceImpl.SERVICE_NAME;

@Component(
        service = ReplicationService.class,
        property = {
                SERVICE_ID + EQUALS + SERVICE_NAME
        }
)
public class ReplicationServiceImpl implements ReplicationService {

    protected static final String SERVICE_NAME = "Replication Service";

    private static final String TAG = ReplicationServiceImpl.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationServiceImpl.class);

    @Reference
    Replicator replicator;

    @Reference
    ResourceResolverService resourceResolverService;

    @Override
    public void replicateContent(String payload) {
        LOGGER.info("{}: trying to replicate: {}", TAG, payload);
        // Getting resource resolver
        ResourceResolver resourceResolver = resourceResolverService.getResourceResolver();
        // Getting the session
        Session session = resourceResolver.adaptTo(Session.class);
        // Replicate the page
        replicate(session, payload);
        // Get all the assets on the page(s)
        Set<String> assetsOnPage = getAssetsOnPage(resourceResolver, payload);
        for (String assetPath : assetsOnPage) {
            replicate(session, assetPath);
        }
        LOGGER.info("{}: replication completed successfully", TAG);
    }

    private Set<String> getAssetsOnPage(ResourceResolver resourceResolver, String payload) {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = Objects.requireNonNull(pageManager).getPage(payload);
        if (page == null) {
            return new LinkedHashSet<>();
        }
        Resource resource = page.getContentResource();
        AssetReferenceSearch assetReferenceSearch = new AssetReferenceSearch(resource.adaptTo(Node.class),
                MOUNTPOINT_ASSETS, resourceResolver);
        Map<String, Asset> assetMap = assetReferenceSearch.search();
        return assetMap.keySet();
    }

    private void replicate(Session session, String path) {
        try {
            LOGGER.info("{}: Replicating: {}", TAG, path);
            replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
        } catch (ReplicationException e) {
            LOGGER.error("{}: replication failed due to: {}", TAG, e.getMessage());
        }
    }
}
