package ru.bicev.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.eclipse.microprofile.faulttolerance.Retry;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MonitoringHttpClient {

    @Inject
    @ConfigProperty(name = "monitoring.http.timeout-seconds")
    int httpTimeoutSeconds;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Retry(maxRetries = 3, delay = 1000, retryOn = { IOException.class, InterruptedException.class })
    public HttpResponse<String> executeNetworkRequestWithRetry(String url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(httpTimeoutSeconds))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
