package ru.bicev.repo;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ru.bicev.entity.MonitoredService;

@ApplicationScoped
public class MonitoredServiceRepository implements PanacheRepository<MonitoredService> {

    public List<MonitoredService> findActive() {
        return list("active", true);
    }

    public List<MonitoredService> findInactive() {
        return list("active", false);
    }

    public List<MonitoredService> findNameLike(String name) {
        return list("lower(name) like lower(?1)", "%" + name + "%");
    }

    public List<MonitoredService> findUrlLike(String url) {
        return list("lower(url) like lower(?1)", "%" + url + "%");
    }

    public List<MonitoredService> findReadyToCheck() {
        var active = list("active = true and broken = false");
        LocalDateTime now = LocalDateTime.now();
        return active.stream()
                .filter(s -> s.lastChecked == null || s.lastChecked.plusSeconds(s.checkIntervalSeconds).isBefore(now))
                .toList();
    }

    public List<MonitoredService> findBrokenServices() {
        return list("active = true and broken = true");
    }

}
