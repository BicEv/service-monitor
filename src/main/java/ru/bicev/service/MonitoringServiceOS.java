package ru.bicev.service;

import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;

import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.entity.MonitoredService;

//This class is written for educational purposes and is not intended for production use.
//@ApplicationScoped
public class MonitoringServiceOS {

    @Inject
    HealthCheckService healthCheckService;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public void checkAllServiceManually() {
        List<MonitoredService> services = MonitoredService.findReadyToCheck();

        if (services.isEmpty()) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var service : services) {
                executor.submit(() -> {
                    QuarkusTransaction.joiningExisting().run(() -> {
                        MonitoredService managedService = MonitoredService.findById(service.id);

                        HealthCheckLog log = new HealthCheckLog();
                        log.service = managedService;
                        log.checkedAt = LocalDateTime.now();
                        long start = System.currentTimeMillis();

                        try {
                            var request = HttpRequest.newBuilder().uri(URI.create(managedService.url))
                                    .timeout(Duration.ofSeconds(10)).GET().build();

                            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                            log.statusCode = response.statusCode();
                            log.isSuccess = (log.statusCode == managedService.expectedStatusCode);
                        } catch (HttpTimeoutException e) {
                            log.isSuccess = false;
                            log.failureReason = "Timeout: service did not respond within expected time.";
                        } catch (UnknownHostException e) {
                            log.isSuccess = false;
                            log.failureReason = "DNS error: unable to find host.";
                        } catch (ConnectException e) {
                            log.isSuccess = false;
                            log.failureReason = "Connection refused: service is not accepting connections.";
                        } catch (Exception e) {
                            log.isSuccess = false;
                            log.failureReason = e.getMessage();
                        } finally {
                            log.responseTimeMs = System.currentTimeMillis() - start;
                            managedService.lastChecked = LocalDateTime.now();

                            log.persist();
                        }

                    });
                });
            }
        }

    }

}
