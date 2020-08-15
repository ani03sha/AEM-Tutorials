package org.redquark.aem.tutorials.core.services.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.redquark.aem.tutorials.core.services.ReferencedAssetService;
import org.redquark.aem.tutorials.core.services.ResourceResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.Map;
import java.util.Objects;

import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;

@Component(
        service = ReferencedAssetService.class,
        property = {
                Constants.SERVICE_ID + EQUALS + "Referenced Asset Service",
                Constants.SERVICE_DESCRIPTION + EQUALS + "Returns all the assets referenced"
        }
)
public class ReferencedAssetServiceImpl implements ReferencedAssetService {

    private static final String TAG = ReferencedAssetService.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencedAssetService.class);

    @Reference
    ResourceResolverService resourceResolverService;

    @Override
    public Map<String, Asset> getReferencedAssets(String pagePath) {
        LOGGER.debug("{}: Searching assets referenced on page path: {}", TAG, pagePath);
        // Get the resource resolver
        ResourceResolver resourceResolver = resourceResolverService.getResourceResolver();
        // Get the resource instance representing the path
        Resource resource = resourceResolver.getResource(pagePath);
        // Adapt this resource to the Node
        Node node = Objects.requireNonNull(resource).adaptTo(Node.class);
        // Create an instance of AssetReferenceSearch API
        AssetReferenceSearch assetReferenceSearch = new AssetReferenceSearch(node, DamConstants.MOUNTPOINT_ASSETS, resourceResolver);
        // Getting all the assets referenced
        Map<String, Asset> referencedAssets = assetReferenceSearch.search();
        LOGGER.debug("{}: number of assets found on page: {} are {}", TAG, pagePath, referencedAssets.size());

        return referencedAssets;
    }
}
