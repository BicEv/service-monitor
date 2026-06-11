package ru.bicev.service;

import java.util.concurrent.Executor;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.bicev.entity.MonitoredService;

@ApplicationScoped
public class MonitoringService {

    @Inject
    private HealthCheckService healthCheckService;

    @Inject
    @VirtualThreads
    private Executor virtualThreadExecutor;

    @Scheduled(every = "10s")
    public void checkAllServices() {
        MonitoredService.findReadyToCheck().forEach(service -> {
            virtualThreadExecutor.execute(() -> healthCheckService.performCheck(service));
        });
    }

}
