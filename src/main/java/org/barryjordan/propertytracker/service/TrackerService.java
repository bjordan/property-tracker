package org.barryjordan.propertytracker.service;

import org.barryjordan.propertytracker.crawler.Crawler;
import org.barryjordan.propertytracker.repository.PropertyRepository;

import java.util.List;

/**
 *  Runs through list of Crawlers and persists the results.
 */
public class TrackerService {
    private List<Crawler> crawlers;
    private PropertyRepository propertyRepository;

    /**
     * Creates a new instance.
     *
     * @param propertyRepository Used to persist properties.
     * @param crawlers Provider specific crawlers.
     */
    public TrackerService(final PropertyRepository propertyRepository, final List<Crawler> crawlers) {
        this.crawlers = crawlers;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Executes crawlers and persists results.
     */
    public void track() {
        for (Crawler crawler : crawlers) {
            propertyRepository.saveProperties(crawler.crawl());
        }
    }
}
