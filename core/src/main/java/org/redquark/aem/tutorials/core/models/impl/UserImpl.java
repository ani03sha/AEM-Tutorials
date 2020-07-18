package org.redquark.aem.tutorials.core.models.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.redquark.aem.tutorials.core.models.User;
import org.redquark.aem.tutorials.core.utils.IDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Objects;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {User.class},
        resourceType = {UserImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class UserImpl implements User {

    protected static final String RESOURCE_TYPE = "aemtutorials/components/content/user";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserImpl.class);
    private static final String TAG = UserImpl.class.getSimpleName();

    @ValueMapValue
    String id;

    @ValueMapValue
    String name;

    @ValueMapValue
    String gender;

    @ValueMapValue
    String address;

    @SlingObject
    SlingHttpServletRequest request;

    @PostConstruct
    protected void init() {
        // Get the unique id from generator
        String generatedId = IDGenerator.generateUniqueID(8);
        LOGGER.debug("{}: Generated id is: {}", TAG, generatedId);
        // Getting the reference of the current node
        Node currentNode = request.getResource().adaptTo(Node.class);
        // Stored id, if any
        String storedId;
        // Getting the current session
        Session session = request.getResourceResolver().adaptTo(Session.class);
        try {
            if (currentNode != null && !currentNode.hasProperty("id")) {
                currentNode.setProperty("id", generatedId);
            } else {
                // Getting the stored id from the node
                storedId = Objects.requireNonNull(currentNode).getProperty("id").getString();
                if (storedId == null || storedId.isEmpty()) {
                    Objects.requireNonNull(currentNode).setProperty("id", generatedId);
                }
            }
            // Saving the session
            Objects.requireNonNull(session).save();
        } catch (RepositoryException e) {
            LOGGER.error("{}: Error occurred: {}", TAG, e.getMessage());
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public String getAddress() {
        return address;
    }
}
