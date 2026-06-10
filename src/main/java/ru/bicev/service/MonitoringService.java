package ru.bicev.service;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.bicev.entity.MonitoredService;

@ApplicationScoped
public class MonitoringService {

    @Inject
    private HealthCheckService healthCheckService;

    @Scheduled(every = "10s")
    @RunOnVirtualThread
    public void checkAllServices() {
        MonitoredService.findReadyToCheck().forEach(service -> {
            healthCheckService.performCheck(service);
        });
    }

}
