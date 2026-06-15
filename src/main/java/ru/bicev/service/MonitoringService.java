package ru.bicev.service;

import java.util.concurrent.CompletableFuture;
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

    @Scheduled(every = "10s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void checkAllServices() {
        var services = MonitoredService.findReadyToCheck();

        if (services.isEmpty())
            return;

        var futures = services.stream()
        .map(service -> CompletableFuture.runAsync(
            () -> healthCheckService.performCheck(service), 
            virtualThreadExecutor
        ))
        .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }

}
