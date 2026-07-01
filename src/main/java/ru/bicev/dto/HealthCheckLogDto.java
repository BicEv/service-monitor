package ru.bicev.dto;

import java.time.LocalDateTime;

public record HealthCheckLogDto(
        Long id,
        Long serviceId,
        LocalDateTime checkedAt,
        Integer statusCode,
        Long responseTimeMs,
        Boolean isSuccess,
        String failureReason) {

}
