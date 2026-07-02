package ru.bicev.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.bicev.repo.MonitoredServiceRepository;

/**
 * Service that performs health checks on monitored services and recovers broken
 * services.
 * It uses virtual threads to perform checks concurrently and efficiently.
 */
@ApplicationScoped
public class MonitoringService {

    /** Bean of HealthCheckService service */
    @Inject
    private HealthCheckService healthCheckService;

    /** Bean of virtual thread executor */
    @Inject
    @VirtualThreads
    private Executor virtualThreadExecutor;

    /** Bean of monitored service repository */
    @Inject
    private MonitoredServiceRepository serviceRepository;

    /**
     * Scheduled method that checks the health of all services that are ready to be
     * checked.
     * It runs at a fixed interval defined by the configuration property
     * "check.services.job.every".
     */
    @Scheduled(identity = "check-services-job", every = "${check.services.job.every}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void checkAllServices() {
        var services = serviceRepository.findReadyToCheck();

        if (services.isEmpty())
            return;

        var futures = services.stream()
                .map(service -> CompletableFuture.runAsync(
                        () -> healthCheckService.performCheck(service),
                        virtualThreadExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }

    /**
     * Scheduled method that recovers broken services.
     * It runs at a fixed interval defined by the configuration property
     * "recover.services.job.every".
     */
    @Scheduled(identity = "recover-services-job", every = "${recover.services.job.every}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void recoverBrokenServices() {
        var brokenServices = serviceRepository.findBrokenServices();
        if (brokenServices.isEmpty())
            return;

        var futures = brokenServices.stream()
                .map(service -> CompletableFuture.runAsync(
                        () -> healthCheckService.performCheck(service), virtualThreadExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }

}
