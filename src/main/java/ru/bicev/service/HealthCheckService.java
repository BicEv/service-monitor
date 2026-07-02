package ru.bicev.service;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.bicev.dto.ServiceFailureEvent;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.entity.MonitoredService;
import ru.bicev.repo.HealthCheckLogRepository;
import ru.bicev.repo.MonitoredServiceRepository;

/**
 * Service that performs health checks on monitored services and logs the results. It also emits events for service failures.
 */
@ApplicationScoped
public class HealthCheckService {

    /** Bean of Kafka emmitter */
    @Inject
    @Channel("service-failures")
    Emitter<ServiceFailureEvent> failureEmitter;

    /** Bean of MonitoringHttpClient */
    @Inject
    MonitoringHttpClient monitoringHttpClient;

    /** Bean of HealthCheckLogRepository */
    @Inject
    private HealthCheckLogRepository logRepository;

    /** Bean of MonitoredServiceRepository */
    @Inject
    private MonitoredServiceRepository serviceRepository;

    /**
     * Performs a health check on the given monitored service, logs the result, and emits a failure event if the service is found to be broken.
     * @param service   The monitored service to check
     */
    public void performCheck(MonitoredService service) {
        HealthCheckLog log = new HealthCheckLog();
        log.checkedAt = LocalDateTime.now();
        long start = System.currentTimeMillis();
        boolean wasBroken = service.broken != null && service.broken;
        try {

            HttpResponse<String> response = monitoringHttpClient.executeNetworkRequestWithRetry(service.url);

            log.statusCode = response.statusCode();
            log.isSuccess = (log.statusCode.equals(service.expectedStatusCode));
            if (!log.isSuccess) {
                log.failureReason = "Unexpected status code: expected " + service.expectedStatusCode + " but got "
                        + log.statusCode;
            }
        } catch (Exception e) {
            handleException(log, e);
        } finally {
            log.responseTimeMs = System.currentTimeMillis() - start;
            QuarkusTransaction.requiringNew().run(() -> {
                MonitoredService managedService = serviceRepository.findById(service.id);
                if (managedService != null) {
                    managedService.lastChecked = LocalDateTime.now();
                    managedService.broken = !log.isSuccess;
                    log.service = managedService;
                    logRepository.persist(log);
                }
            });

        }
        if (!log.isSuccess && !wasBroken) {
            failureEmitter.send(new ServiceFailureEvent(
                    service.id,
                    service.name,
                    service.url,
                    log.statusCode,
                    log.failureReason, LocalDateTime.now()));
        }

    }

    /**
     * Handles exceptions that occur during the health check process, updating the log with appropriate failure reasons and status codes.
     * @param log   The health check log to update with failure information
     * @param e     Exception that occurred during the health check
     */
    private void handleException(HealthCheckLog log, Exception e) {
        log.isSuccess = false;

        switch (e) {
            case HttpTimeoutException ex -> {
                log.failureReason = "Timeout: service did not respond within expected time.";
                log.statusCode = 408;
            }
            case UnknownHostException ex -> {
                log.failureReason = "DNS error: unable to find host.";
                log.statusCode = 502;
            }
            case ConnectException ex -> {
                log.failureReason = "Connection refused: service is not accepting connections.";
                log.statusCode = 503;
            }

            default -> {
                log.failureReason = e.getMessage();
                log.statusCode = 500;
            }

        }
    }
}
