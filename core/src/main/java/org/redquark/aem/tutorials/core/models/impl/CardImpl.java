package org.redquark.aem.tutorials.core.models.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.redquark.aem.tutorials.core.models.Card;
import org.redquark.aem.tutorials.core.services.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(
        adaptables = {Resource.class},
        adapters = {Card.class},
        resourceType = {CardImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CardImpl implements Card {

    protected static final String RESOURCE_TYPE = "aemtutorials/components/content/card";
    private static final Logger LOGGER = LoggerFactory.getLogger(CardImpl.class);
    private static final String TAG = CardImpl.class.getSimpleName();

    @Inject
    String cardTitle;

    @Inject
    String buttonText;

    @OSGiService
    CardService cardService;

    private String organizationName;

    private String homepageURL;

    @PostConstruct
    protected void init() {
        organizationName = cardService.getOrganizationName();
        homepageURL = cardService.getHomepageURL();
        LOGGER.info("{}: organization name: {}", TAG, organizationName);
        LOGGER.info("{}: homepage url: {}", TAG, homepageURL);
    }

    @Override
    public String getCardTitle() {
        return cardTitle;
    }

    @Override
    public String getButtonText() {
        return buttonText;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getHomepageURL() {
        return homepageURL;
    }
}
