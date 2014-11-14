package org.barryjordan.propertytracker.crawler;

import org.apache.commons.codec.digest.DigestUtils;
import org.barryjordan.propertytracker.model.PropertyVO;
import org.springframework.web.client.RestOperations;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Crawls provider and returns a list of properties matching criteria.
 */
public abstract class Crawler {

    private final RestOperations restOperations;

    /**
     * Creates a new instance.
     *
     * @param restOperations Used to make HTTP requests.
     */
    public Crawler(final RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    /**
     * Crawl for properties. Request listings from
     * property source and return properties matching criteria.
     */
    public abstract List<PropertyVO> crawl();

    /**
     * Generate a "unique" ID for the property.
     *
     * @param complex The complex e.g. The Park
     * @param number The property number.
     * @param price THe property price.
     * @return An MD5 of arguments.
     */
    String generateID(final String complex, final int number, final int price) {
        return DigestUtils.md5Hex(complex + String.valueOf(number) + String.valueOf(price));
    }

    /**
     * Makes a HTTP request to url and returns HTTP body content.
     *
     * @param url To request.
     * @return HTTP body content.
     */
    String getPageContent(final String url) {
        return restOperations.getForEntity(url, String.class).getBody();
    }

    /**
     * Check if full address contains specific String.
     *
     * @param address The full property address to match against.
     * @return If the address contains the search string.
     */
    boolean isMatch(final String address) {
        return address.toLowerCase(Locale.ENGLISH).contains("larch");
    }

    /**
     * Attempts to extract the complex name from the full address.
     *
     * @param address The full property address to match against.
     * @return The complex, or an empty String.
     */
    String getComplex(final String address) {
        List<String> complexes = Arrays.asList("The Court", "The Park", "The Square", "The Green", "The Crescent");
        for (String complex : complexes) {
            if (address.toLowerCase(Locale.ENGLISH).contains(complex.toLowerCase(Locale.ENGLISH))) {
                return complex;
            }
        }
        return "";
    }

    /**
     * Attempts to extract the property number from the full address.
     * Assumes that the number is the first segment of the address.
     *
     * @param address The full property address to match against.
     * @return The property number, or -1.
     */
    int getNumber(final String address) {
        try {
            return Integer.parseInt(address.split(" ")[0].trim());
        } catch(NumberFormatException e) {
            return -1;
        }
    }
}
