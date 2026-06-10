package ru.bicev.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.entity.MonitoredService;

@ApplicationScoped
public class HealthCheckService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @RunOnVirtualThread
    @Transactional
    public void performCheck(MonitoredService service) {
        HealthCheckLog log = new HealthCheckLog();
        log.service = service;
        log.checkedAt = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(service.url))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.statusCode = response.statusCode();
            log.isSuccess = (log.statusCode == service.expectedStatusCode);
            log.responseTimeMs = System.currentTimeMillis() - startTime;

        } catch (Exception e) {
            log.isSuccess = false;
            log.failureReason = e.getMessage();
        }

        MonitoredService.update("lastChecked = ?1 where id = ?2", LocalDateTime.now(), service.id);
        log.persist();

    }

}
