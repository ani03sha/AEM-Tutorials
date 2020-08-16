package org.redquark.aem.tutorials.core.workflows.process;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.redquark.aem.tutorials.core.services.ReferencedAssetService;
import org.redquark.aem.tutorials.core.services.ResourceResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.PROCESS_LABEL;
import static org.redquark.aem.tutorials.core.workflows.process.UpdateReferencedAssetsWorkflow.PROCESS_LABEL_VALUE;

@Component(
        service = WorkflowProcess.class,
        property = {
                PROCESS_LABEL + EQUALS + PROCESS_LABEL_VALUE
        }
)
public class UpdateReferencedAssetsWorkflow implements WorkflowProcess {

    protected static final String PROCESS_LABEL_VALUE = "Update Referenced Asset";
    private static final String TAG = UpdateReferencedAssetsWorkflow.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateReferencedAssetsWorkflow.class);

    @Reference
    ReferencedAssetService referencedAssetService;

    @Reference
    ResourceResolverService resourceResolverService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        // Getting payload from Workflow - workItem -> workflowData -> payload
        String payloadType = workItem.getWorkflowData().getPayloadType();
        LOGGER.debug("{}: Payload type: {}", TAG, payloadType);
        // Check type of payload; there are two - JCR_PATH and JCR_UUID
        if (StringUtils.equals(payloadType, "JCR_PATH")) {
            // Get the JCR path from the payload
            String path = workItem.getWorkflowData().getPayload().toString();
            LOGGER.debug("{}: Payload path: {}", TAG, path);
            // Get process arguments which will contain the properties to update
            String[] processArguments = metaDataMap.get("PROCESS_ARGS", "default").split("=");
            // Get the referenced assets
            Map<String, Asset> referencedAssets = referencedAssetService.getReferencedAssets(path);
            LOGGER.debug("{}: Starting updating asset metadata with following values: {}", TAG, Arrays.toString(processArguments));
            // Get resource resolver
            ResourceResolver resourceResolver = resourceResolverService.getResourceResolver();
            try {
                // Loop for each referenced asset
                for (Map.Entry<String, Asset> entry : referencedAssets.entrySet()) {
                    // Asset path
                    String assetPath = entry.getKey();
                    LOGGER.debug("{}: Updating metadata for asset: {}", TAG, assetPath);
                    // Get resource corresponding to the path of asset
                    Resource assetResource = resourceResolver.getResource(assetPath);
                    // Get the metadata for the asset resource
                    Resource assetResourceMetadata = Objects.requireNonNull(assetResource).getChild("jcr:content/metadata");
                    // Get metadata properties as modifiable map
                    ModifiableValueMap modifiableValueMap = Objects.requireNonNull(assetResourceMetadata).adaptTo(ModifiableValueMap.class);
                    // Add the new property
                    Objects.requireNonNull(modifiableValueMap).put(processArguments[0], processArguments[1]);
                }
                resourceResolver.commit();
            } catch (PersistenceException e) {
                LOGGER.error("{}: exception occurred: {}", TAG, e.getMessage());
            }
        } else {
            LOGGER.warn("{}: payload type - {} is not valid", TAG, payloadType);
        }
    }
}
