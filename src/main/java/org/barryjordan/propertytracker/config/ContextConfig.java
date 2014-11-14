package org.barryjordan.propertytracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.barryjordan.propertytracker.crawler.Crawler;
import org.barryjordan.propertytracker.crawler.DaftCrawler;
import org.barryjordan.propertytracker.crawler.MyHomeCrawler;
import org.barryjordan.propertytracker.repository.PropertyRepository;
import org.barryjordan.propertytracker.repository.PropertyRepositoryImpl;
import org.barryjordan.propertytracker.scheduler.SchedulerInit;
import org.barryjordan.propertytracker.service.*;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Default Spring Context Configuration.
 */
@Configuration
public class ContextConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextConfig.class);

    @Bean
    public Crawler daftCrawler(final RestOperations restOperations) {
       return new DaftCrawler(restOperations);
    }

    @Bean
    public Crawler myHomeCrawler(final RestOperations restOperations) {
        return new MyHomeCrawler(restOperations);
    }

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        return taskScheduler;
    }

    @Bean
    public TrackerService trackerService(final List<Crawler> crawlers, final PropertyRepository propertyRepository) {
        return new TrackerService(propertyRepository, crawlers);
    }

    @Bean
    public SchedulerInit schedulerInit(final TrackerService trackerService, final TaskScheduler taskScheduler) {
        return new SchedulerInit(trackerService, taskScheduler);
    }

    @Bean
    public PropertyRepository propertyRepository(final ObjectMapper objectMapper, final Client elasticClient) {
        return new PropertyRepositoryImpl(objectMapper, elasticClient);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Client elasticClient() {
        Node node = NodeBuilder.nodeBuilder().client(true).node();
        Client client = node.client();
        try {
            CreateIndexResponse response = client.admin().indices().prepareCreate("properties").execute().actionGet();
            if (!response.isAcknowledged()) {
                throw new IllegalStateException("Failed to create ElasticSearch index");
            }
        } catch (IndexAlreadyExistsException e) {
            LOGGER.info("Index 'properties' already exists");
        }
        return client;
    }
}
