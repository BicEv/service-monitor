package ru.bicev.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 
 * MonitoredServiceDto DTO for transferring monitored service data between layers
 * @param id                            unique identifier of the service
 * @param name                          name of the service
 * @param url                           URL of the service to be monitored
 * @param checkIntervalSeconds          interval in seconds at which the service should be checked
 * @param expectedStatusCode            expected HTTP status code for a successful health check
 * @param active                        indicates whether the service is being monitored
 */
public record MonitoredServiceDto(
        Long id,
        @NotBlank(message = "Name must be present") String name,
        @URL(message = "URL must be a valid URL") String url,
        @NotNull @Min(value = 1, message = "Check interval must be a positive integer") Integer checkIntervalSeconds,
        @NotNull @Min(100) @Max(599) Integer expectedStatusCode,
        @NotNull Boolean active) {

}
