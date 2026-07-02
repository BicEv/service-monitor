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

/**
 * A client for executing network requests with retry logic. This class uses the
 * Java HttpClient to send HTTP requests and handles retries for specified
 * exceptions.
 */
@ApplicationScoped
public class MonitoringHttpClient {

    /** The timeout for HTTP requests in seconds. Default value is 5 */
    @Inject
    @ConfigProperty(name = "monitoring.http.timeout-seconds")
    int httpTimeoutSeconds;

    /**
     * The HttpClient instance used for sending HTTP requests. It is configured with
     * a
     * connection timeout of 5 seconds.
     */
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * Executes a network request to the specified URL with retry logic. If the
     * request fails due to an IOException or InterruptedException, it will be
     * retried up to 3 times with a delay of 1000 milliseconds between attempts. The
     * method returns the HTTP response as a string.
     * 
     * @param url URL to which the network request is sent
     * @return The HTTP response received from the network request
     * @throws IOException          If an I/O error occurs when sending or receiving
     *                              the request
     * @throws InterruptedException If an error occurs while waiting for the
     *                              response
     */
    @Retry(maxRetries = 3, delay = 1000, retryOn = { IOException.class, InterruptedException.class })
    public HttpResponse<String> executeNetworkRequestWithRetry(String url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(httpTimeoutSeconds))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
