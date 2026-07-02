package ru.bicev.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.bicev.repo.MonitoredServiceRepository;

@ApplicationScoped
public class MonitoringService {

    @Inject
    private HealthCheckService healthCheckService;

    @Inject
    @VirtualThreads
    private Executor virtualThreadExecutor;

    @Inject
    private MonitoredServiceRepository serviceRepository;

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
