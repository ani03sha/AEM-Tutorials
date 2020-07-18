package org.redquark.aem.tutorials.core.models;

/**
 * @author Anirudh Sharma
 * <p>
 * Represents the "User" component
 */
public interface User {

    /**
     * @return unique id of the user
     */
    String getId();

    /**
     * @return String to represent the name of the user
     */
    String getName();

    /**
     * @return String to represent the gender of the user
     */
    String getGender();

    /**
     * @return String to represent the address of the user
     */
    String getAddress();

}
