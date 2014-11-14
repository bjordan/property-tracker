package org.barryjordan.propertytracker.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.barryjordan.propertytracker.model.PropertyVO;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

/**
 * PropertyRepository implementation.
 */
public class PropertyRepositoryImpl implements PropertyRepository, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyRepositoryImpl.class);
    private final ObjectMapper objectMapper;
    private final Client elasticClient;
    private static final String ELASTIC_INDEX_PROPERTIES = "properties";
    private static final String ELASTIC_TYPE_PROPERTY = "property";

    /**
     * Creates a new instance.
     *
     * @param objectMapper To convert object to/from JSON.
     * @param elasticClient Client to access elasticsearch data store.
     */
    public PropertyRepositoryImpl(final ObjectMapper objectMapper, final Client elasticClient) {
        this.objectMapper = objectMapper;
        this.elasticClient = elasticClient;
    }

    @Override
    public void saveProperties(final List<PropertyVO> properties) {
        BulkRequestBuilder bulkRequest = elasticClient.prepareBulk();

        try {
            for (PropertyVO property : properties) {
                if (!exists(property.getPropertyID())) {
                    bulkRequest.add(elasticClient.prepareIndex(ELASTIC_INDEX_PROPERTIES, ELASTIC_TYPE_PROPERTY, property.getPropertyID())
                            .setTimestamp(String.valueOf(System.currentTimeMillis()))
                            .setSource(objectMapper.writeValueAsString(property)));
                }
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing PropertyVO", e);
        }

        if (bulkRequest.numberOfActions() > 0) {
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse bulkItemResponse : bulkResponse.getItems())
                LOGGER.error(bulkItemResponse.getFailureMessage());
            }
        }
    }

    @Override
    public void saveProperty(final PropertyVO property) {
        try {
            if (!exists(property.getPropertyID())) {
                elasticClient.prepareIndex(ELASTIC_INDEX_PROPERTIES, ELASTIC_TYPE_PROPERTY, property.getPropertyID())
                        .setSource(objectMapper.writeValueAsString(property))
                        .setTimestamp(String.valueOf(System.currentTimeMillis()))
                        .execute()
                        .actionGet();
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing PropertyVO", e);
        }
    }

    @Override
    public boolean exists(final String propertyID) {
        return elasticClient.prepareGet(ELASTIC_INDEX_PROPERTIES, ELASTIC_TYPE_PROPERTY, propertyID)
                .execute()
                .actionGet()
                .isExists();
    }

    @Override
    public void destroy() throws Exception {
        elasticClient.close();
    }
}
