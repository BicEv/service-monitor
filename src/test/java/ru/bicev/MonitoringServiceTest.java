package ru.bicev;

import java.time.Duration;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.bicev.dto.ServiceFailureEvent;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.entity.MonitoredService;
import ru.bicev.repo.HealthCheckLogRepository;
import ru.bicev.repo.MonitoredServiceRepository;
import ru.bicev.service.MonitoringService;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class MonitoringServiceTest {

    @Inject
    MonitoringService monitoringService;

    @Inject
    Scheduler scheduler;

    @Inject
    MonitoredServiceRepository serviceRepository;

    @Inject
    HealthCheckLogRepository logRepository;

    @Inject
    @ConfigProperty(name = "monitoring.http.timeout-seconds")
    int httpTimeoutSeconds;

    @Inject
    @Any
    InMemoryConnector inMemoryConnector;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        HealthCheckLog.deleteAll();
        MonitoredService.deleteAll();

        InMemorySink<ServiceFailureEvent> sink = inMemoryConnector.sink("service-failures");
        if (sink != null) {
            sink.clear();
        }
    }

    // Not sure if i really need to open all these transactions in this test, but
    // without some of them it fails,
    // because the test is running in a different thread than the one that persists
    // the entities, so the transaction context is not propagated.
    // I will leave it like this for now, but if you have a better idea, please let
    // me know.

    @Test
    void testParallelMonitoringAndAlerting() {
        QuarkusTransaction.requiringNew().run(() -> {
            persistTestService("Fake #1", "https://some-non-existing-domain-1.com");
            persistTestService("Fake #2", "https://some-non-existing-domain-2.com");
        });

        InMemorySink<ServiceFailureEvent> failureSink = inMemoryConnector.sink("service-failures");

        monitoringService.checkAllServices();

        await().atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> {
                    long count = QuarkusTransaction.requiringNew().call(() -> logRepository.count());
                    return count == 2;
                });

        List<HealthCheckLog> logs = QuarkusTransaction.requiringNew().call(() -> logRepository.listAll());

        assertEquals(2, logs.size());

        for (HealthCheckLog log : logs) {
            assertTrue(log.checkedAt != null);
            assertEquals(false, log.isSuccess);
            assertTrue(log.failureReason.contains("DNS error") || log.failureReason.contains("Timeout")
                    || log.failureReason.contains("Connection refused"));
        }

        assertEquals(2, failureSink.received().size());
        ServiceFailureEvent firstEvent = failureSink.received().get(0).getPayload();
        assertTrue(firstEvent.failureReason.contains("DNS error")
                || firstEvent.failureReason.contains("Timeout")
                || firstEvent.failureReason.contains("Connection refused"));

    }

    @Test
    void testMonitoring_Success() {
        QuarkusTransaction.requiringNew().run(() -> {
            persistTestService("Local-App-Check", "http://localhost:8081/api/v1/services");
        });

        scheduler.resume();

        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(200))
                .until(() -> {
                    long count = QuarkusTransaction.requiringNew().call(() -> logRepository.count());
                    return count >= 1;
                });

        scheduler.pause();

        List<HealthCheckLog> logs = QuarkusTransaction.requiringNew().call(() -> logRepository.listAll());

        assertTrue(logs.size() >= 1);
        assertEquals(true, logs.get(0).isSuccess, "Checking must be successful (isSuccess = true)");
        assertEquals(200, logs.get(0).statusCode);
    }

    @Test
    void testSchedulerSkipsOverlappingRuns() {
        QuarkusTransaction.requiringNew().run(() -> {
            persistTestService("Slow-service", "http://10.255.255.1");
        });

        scheduler.resume();

        try {
            Thread.sleep(httpTimeoutSeconds * 2000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            scheduler.pause();
        }

        await().atMost(Duration.ofSeconds(httpTimeoutSeconds + 15))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> {
                    long count = QuarkusTransaction.requiringNew().call(() -> logRepository.count());
                    return count >= 1;
                });

        long finalCount = QuarkusTransaction.requiringNew().call(() -> logRepository.count());
        assertEquals(1, finalCount, "Scheduler must skip all overlapping runs, leaving exactly 1 log");
    }

    void persistTestService(String name, String url) {
        MonitoredService service = new MonitoredService();
        service.name = name;
        service.url = url;
        service.active = true;
        service.expectedStatusCode = 200;
        service.checkIntervalSeconds = 10;
        serviceRepository.persist(service);
    }

}
