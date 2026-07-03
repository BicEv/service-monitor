package ru.bicev.repo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ru.bicev.entity.MonitoredService;

/**
 * Repository for managing MonitoredService entities.
 */
@ApplicationScoped
public class MonitoredServiceRepository implements PanacheRepository<MonitoredService> {

    /**
     * Find all active monitored services.
     * 
     * @return List of active MonitoredService entities.
     */
    public List<MonitoredService> findActive() {
        return list("active", true);
    }

    /**
     * Find all inactive monitored services.
     * 
     * @return List of inactive MonitoredService entities.
     */
    public List<MonitoredService> findInactive() {
        return list("active", false);
    }

    /**
     * Finds all monitored services with names similar to the provided name.
     * 
     * @param name The name to search for
     * @return List of MonitoredService entities with names similar to the provided
     *         name
     */
    public List<MonitoredService> findNameLike(String name) {
        return list("lower(name) like lower(?1)", "%" + name + "%");
    }

    /**
     * Finds all monitored services with names similar to the provided URL.
     * 
     * @param url URL to search for
     * @return List of MonitoredService entities with URL similar to the provided
     *         URL
     */
    public List<MonitoredService> findUrlLike(String url) {
        return list("lower(url) like lower(?1)", "%" + url + "%");
    }

    /**
     * Finds all monitored services with the name and URL similar to the provided
     * name and URL
     * 
     * @param name Name to search for
     * @param url  URL to search for
     * @return List of MonitoredService entities with similar URL and name to the
     *         provided URL and name
     */
    public List<MonitoredService> search(String name, String url) {
        if ((name == null || name.isBlank()) && (url == null || url.isBlank())) {
            return List.of();
        }
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (name != null && !name.isBlank()) {
            query.append(" and lower(name) like lower(:name)");
            params.put("name", "%" + name.trim() + "%");
        }

        if (url != null && !url.isBlank()) {
            query.append(" and lower(url) like lower(:url)");
            params.put("url", "%" + url.trim() + "%");
        }

        return list(query.toString(), params);
    }

    /**
     * Finds all active and not broken monitored services and filters them based in
     * their last checked time and check interval.
     * 
     * @return List of MonitoredService entities that are ready to be checked
     */
    public List<MonitoredService> findReadyToCheck() {
        var active = list("active = true and broken = false");
        LocalDateTime now = LocalDateTime.now();
        return active.stream()
                .filter(s -> s.lastChecked == null || s.lastChecked.plusSeconds(s.checkIntervalSeconds).isBefore(now))
                .toList();
    }

    /**
     * Finds all monitored services that are marked as broken.
     * 
     * @return List of MonitoredService entities that are broken
     */
    public List<MonitoredService> findBrokenServices() {
        return list("active = true and broken = true");
    }

}
