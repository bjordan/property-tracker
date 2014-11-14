package org.barryjordan.propertytracker.crawler;

import org.barryjordan.propertytracker.model.PropertySource;
import org.barryjordan.propertytracker.model.PropertyType;
import org.barryjordan.propertytracker.model.PropertyVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Crawler implementation for Daft property website.
 */
public class DaftCrawler extends Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaftCrawler.class);
    private static final String BASE_URL ="http://www.daft.ie";
    private static final String RENT_URL =
            BASE_URL + "/dublin-city/houses-to-rent/santry/?s[area_type]=on&s[mnb]=2&s[advanced]=1&s[pt_id][0]=1&s[furn]=1";
    private static final String SALE_URL =
            BASE_URL + "/dublin-city/houses-for-sale/santry/?s[area_type]=on&s[mnb]=2&s[advanced]=1&s[pt_id][0]=2";

    /**
     * Creates a new instance.
     *
     * @param restOperations Used to make HTTP requests.
     */
    public DaftCrawler(final RestOperations restOperations) {
        super(restOperations);
    }

    @Override
    public List<PropertyVO> crawl() {
        List<PropertyVO> props = new ArrayList<>();

        getListings(props, RENT_URL, PropertyType.RENT);
        getListings(props, SALE_URL, PropertyType.SALE);

        return props;
    }

    /**
     * Recursively search through listings pages
     * for properties that match criteria.
     *
     * @param props List to populate.
     * @param url The Daft listing page URL.
     * @param propertyType Sales or Rentals.
     */
    private void getListings(final List<PropertyVO> props, final String url, final PropertyType propertyType) {
        Document doc = Jsoup.parse(getPageContent(url));

        Elements propertyNodes = doc.select("div.box");
        Element nextPageNode = doc.select("li.next_page").first();

        props.addAll(parseListing(propertyNodes, propertyType));

        if (nextPageNode != null) {
            String nextPath = nextPageNode.childNode(1).attr("href");
            String nextUrl = "";
            try {
                nextUrl = BASE_URL + URLDecoder.decode(nextPath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Can't decode next page URL", e);
            }
            getListings(props, nextUrl, propertyType);
        }
    }

    /**
     * Parses out the details of the property for a list of Elements.
     *
     * @param propertyNodes Nodes containing property details.
     * @param propertyType Sales or Rentals.
     * @return A List of properties matching criteria.
     */
    private List<PropertyVO> parseListing(final Elements propertyNodes, final PropertyType propertyType) {
        List<PropertyVO> properties = new ArrayList<>();

        for (Element propertyNode : propertyNodes) {
            if (propertyNode.childNode(1).toString().contains("sr_counter")) {
                Element fullAddressElem = (Element) propertyNode.childNode(1).childNode(3);
                String url = fullAddressElem.attr("href");
                String fullAddress = fullAddressElem.text().split("-")[0].trim();

                if (isMatch(fullAddress)) {
                    Element priceElem = (Element) propertyNode.childNode(6).childNode(1).childNode(1);
                    int infoNode = 3;
                    if (hasPriceChange((Element) propertyNode.childNode(6).childNode(1))) {
                        infoNode = 5;
                    }
                    Element bedsElem = (Element) propertyNode.childNode(6).childNode(1).childNode(infoNode).childNode(3);
                    Element bathsElem = (Element) propertyNode.childNode(6).childNode(1).childNode(infoNode).childNode(5);

                    int price = Integer.parseInt(priceElem.text().split(" ")[0].trim().replace(",", "").substring(1));
                    int beds = Integer.parseInt(bedsElem.text().split(" ")[0]);
                    int baths = Integer.parseInt(bathsElem.text().split(" ")[0]);
                    String complex = getComplex(fullAddress);
                    int number = getNumber(fullAddress);

                    properties.add(
                            new PropertyVO(
                                    generateID(complex, number, price),
                                    propertyType,
                                    PropertySource.DAFT,
                                    fullAddress,
                                    complex,
                                    number,
                                    price,
                                    beds,
                                    baths,
                                    url));
                }
            }
        }

        return properties;
    }

    /**
     * Does the Element contain a price change node.
     *
     * @param node The Element to check against.
     * @return Contains price change node or not.
     */
    private boolean hasPriceChange(final Element node) {
        return node.toString().contains("price-changes");
    }
}
