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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.entity.MonitoredService;

@ApplicationScoped
public class HealthCheckService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Inject
    ReactiveMailer mailer;

    @Inject
    @ConfigProperty(name = "monitoring.alert.email")
    String alertEmail;

    @Inject
    @ConfigProperty(name = "monitoring.http.timeout-seconds")
    int httpTimeoutSeconds;

    public void performCheck(MonitoredService service) {
        HealthCheckLog log = new HealthCheckLog();
        log.checkedAt = LocalDateTime.now();
        long start = System.currentTimeMillis();

        try {
            var request = HttpRequest.newBuilder().uri(URI.create(service.url))
                    .timeout(Duration.ofSeconds(httpTimeoutSeconds)).GET().build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.statusCode = response.statusCode();
            log.isSuccess = (log.statusCode.equals(service.expectedStatusCode));
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
            QuarkusTransaction.requiringNew().run(() -> {
                MonitoredService managedService = MonitoredService.findById(service.id);
                if (managedService != null) {
                    managedService.lastChecked = LocalDateTime.now();
                    log.service = managedService;
                    log.persist();
                }
            });

        }
        if (alertEmail != null && !alertEmail.isBlank() && !log.isSuccess) {
            sendAlertEmail(service, log);
        }

    }

    private void sendAlertEmail(MonitoredService service, HealthCheckLog log) {
        String reason = log.failureReason != null ? log.failureReason : "Status code: " + log.statusCode;

        mailer.send(Mail.withText(alertEmail,
                "ALERT: Service " + service.name + " is DOWN!",
                String.format("Service URL: %s\nChecked at: %s\nReason: %s\nResponse time: %d ms\nException reason: %s",
                        service.url,
                        log.checkedAt, reason, log.responseTimeMs, log.failureReason)))
                .subscribe().with(success -> {
                },
                        failure -> System.err.println("Failed to send alert email: " + failure.getMessage()));
    }
}
