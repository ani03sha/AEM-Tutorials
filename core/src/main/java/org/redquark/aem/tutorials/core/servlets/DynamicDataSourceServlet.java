package org.redquark.aem.tutorials.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;
import static org.redquark.aem.tutorials.core.constants.AppConstants.COLOR_LIST;
import static org.redquark.aem.tutorials.core.constants.AppConstants.COLOR_LIST_PATH;
import static org.redquark.aem.tutorials.core.constants.AppConstants.COUNTRY_LIST;
import static org.redquark.aem.tutorials.core.constants.AppConstants.COUNTRY_LIST_PATH;
import static org.redquark.aem.tutorials.core.constants.AppConstants.DATASOURCE;
import static org.redquark.aem.tutorials.core.constants.AppConstants.DROPDOWN_SELECTOR;
import static org.redquark.aem.tutorials.core.constants.AppConstants.EQUALS;
import static org.redquark.aem.tutorials.core.constants.AppConstants.FONT_LIST;
import static org.redquark.aem.tutorials.core.constants.AppConstants.FONT_LIST_PATH;
import static org.redquark.aem.tutorials.core.servlets.DynamicDataSourceServlet.RESOURCE_TYPE;
import static org.redquark.aem.tutorials.core.servlets.DynamicDataSourceServlet.SERVICE_NAME;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_ID + EQUALS + SERVICE_NAME,
                SLING_SERVLET_RESOURCE_TYPES + EQUALS + RESOURCE_TYPE
        }
)
public class DynamicDataSourceServlet extends SlingSafeMethodsServlet {

    protected static final String SERVICE_NAME = "Dynamic DataSource Servlet";
    protected static final String RESOURCE_TYPE = "/apps/aemtutorials/dropdowns";
    private static final long serialVersionUID = 4235730140092283425L;
    private static final String TAG = DynamicDataSourceServlet.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            // Getting resource resolver from the current request
            ResourceResolver resourceResolver = request.getResourceResolver();
            // Get the resource object for the path from where the request is fired
            Resource currentResource = request.getResource();
            // Get the dropdown selector
            String dropdownSelector = Objects.requireNonNull(currentResource.getChild(DATASOURCE))
                    .getValueMap()
                    .get(DROPDOWN_SELECTOR, String.class);
            // Get json resource based on the dropdown selector
            Resource jsonResource = getJsonResource(resourceResolver, Objects.requireNonNull(dropdownSelector));
            // Converting this json resource to an Asset
            Asset asset = DamUtil.resolveToAsset(jsonResource);
            // Get the original rendition
            Rendition originalAsset = Objects.requireNonNull(asset).getOriginal();
            // Adapt this to InputStream
            InputStream content = Objects.requireNonNull(originalAsset).adaptTo(InputStream.class);
            // Read all the data in the json file as a string
            StringBuilder jsonContent = new StringBuilder();
            BufferedReader jsonReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(content), StandardCharsets.UTF_8));
            // Loop through each line
            String line;
            while ((line = jsonReader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            Map<String, String> data = new TreeMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                data.put(jsonArray.getJSONObject(i).getString("text"),
                        jsonArray.getJSONObject(i).getString("value"));
            }
            // Creating the data source object
            @SuppressWarnings({"unchecked", "rawtypes"})
            DataSource ds = new SimpleDataSource(new TransformIterator<>(data.keySet().iterator(), (Transformer) o -> {
                String dropValue = (String) o;
                ValueMap vm = new ValueMapDecorator(new HashMap<>());
                vm.put("text", dropValue);
                vm.put("value", data.get(dropValue));
                return new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm);
            }));
            request.setAttribute(DataSource.class.getName(), ds);
        } catch (IOException | JSONException e) {
            LOGGER.error("{}: exception occurred: {}", TAG, e.getMessage());
        }
    }

    private Resource getJsonResource(ResourceResolver resourceResolver, String dropdownSelector) {
        Resource jsonResource;
        switch (dropdownSelector) {
            case COUNTRY_LIST:
                jsonResource = resourceResolver.getResource(COUNTRY_LIST_PATH);
                break;
            case COLOR_LIST:
                jsonResource = resourceResolver.getResource(COLOR_LIST_PATH);
                break;
            case FONT_LIST:
                jsonResource = resourceResolver.getResource(FONT_LIST_PATH);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dropdownSelector);
        }
        return jsonResource;
    }
}
