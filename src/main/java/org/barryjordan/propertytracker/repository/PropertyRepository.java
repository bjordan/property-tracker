package org.barryjordan.propertytracker.repository;

import org.barryjordan.propertytracker.model.PropertyVO;

import java.util.List;

/**
 * Repository for manipulating property data.
 */
public interface PropertyRepository {

    /**
     * Saves properties to data store.
     *
     * @param properties A List of PropertyVO to save.
     */
    void saveProperties(List<PropertyVO> properties);

    /**
     * Saves a property to data store.
     *
     * @param property A PropertyVO to save.
     */
    void saveProperty(PropertyVO property);

    /**
     * Checks if a property with propertyID
     * exists in the repository.
     *
     * @param propertyID Unique property ID.
     * @return Exists or not.
     */
    boolean exists(String propertyID);
}
