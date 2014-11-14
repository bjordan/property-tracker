package org.barryjordan.propertytracker.model;

/**
 * VO representing a property.
 */
public class PropertyVO {
    private final String propertyID;
    private final PropertyType propertyType;
    private final PropertySource propertySource;
    private final String url;
    private final String fullAddress;
    private final String complex;
    private final int number;
    private final int price;
    private final int bedrooms;
    private final int bathrooms;
    private long timestamp;

    public PropertyVO(
            final String propertyID,
            final PropertyType propertyType,
            final PropertySource propertySource,
            final String fullAddress,
            final String complex,
            final int number,
            final int price,
            final int bedrooms,
            final int bathrooms,
            final String url) {
        this.propertyID = propertyID;
        this.propertyType = propertyType;
        this.propertySource = propertySource;
        this.fullAddress = fullAddress;
        this.complex = complex;
        this.number = number;
        this.price = price;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.url = url;

        this.timestamp = System.currentTimeMillis();
    }

    public String getPropertyID() {
        return propertyID;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public PropertySource getPropertySource() {
        return propertySource;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getComplex() {
        return complex;
    }

    public int getNumber() {
        return number;
    }

    public int getPrice() {
        return price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "PropertyVO{" +
                "propertyID='" + propertyID + '\'' +
                ", propertyType=" + propertyType +
                ", propertySource=" + propertySource +
                ", url='" + url + '\'' +
                ", fullAddress='" + fullAddress + '\'' +
                ", complex='" + complex + '\'' +
                ", number=" + number +
                ", price=" + price +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", timestamp=" + timestamp +
                '}';
    }
}
