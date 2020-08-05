package org.redquark.aem.tutorials.core.models;

public interface Card {

    /**
     * @return the title of the card
     */
    String getCardTitle();

    /**
     * @return the text on the button
     */
    String getButtonText();

    /**
     * @return organization name from the osgi config
     */
    String getOrganizationName();

    /**
     * @return homepage url from the osgi config
     */
    String getHomepageURL();
}
