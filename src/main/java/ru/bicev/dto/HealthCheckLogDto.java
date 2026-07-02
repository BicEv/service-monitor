package ru.bicev.dto;

import java.time.LocalDateTime;

/**
 * 
 * HealthCheckLogDto DTO for retrieving information about health check logs.
 * @param id                    unique identifier of health check log entry
 * @param serviceId             identifier of the service being checked
 * @param checkedAt             timestamp of when the health check was performed
 * @param statusCode            status code return by the health check
 * @param responseTimeMs        response time in milliseconds for the health check
 * @param isSuccess             indicates whether the health check was successful or not
 * @param failureReason         reason for failure if the health check was not successful
 */
public record HealthCheckLogDto(
        Long id,
        Long serviceId,
        LocalDateTime checkedAt,
        Integer statusCode,
        Long responseTimeMs,
        Boolean isSuccess,
        String failureReason) {

}
