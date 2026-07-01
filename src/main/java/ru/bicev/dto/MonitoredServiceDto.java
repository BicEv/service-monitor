package ru.bicev.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MonitoredServiceDto(
        Long id,
        @NotBlank(message = "Name must be present") String name,
        @URL(message = "URL must be a valid URL") String url,
        @NotNull @Min(value = 1, message = "Check interval must be a positive integer") Integer checkIntervalSeconds,
        @NotNull @Min(100) @Max(599) Integer expectedStatusCode,
        @NotNull Boolean active) {

}
