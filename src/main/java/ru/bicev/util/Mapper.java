package ru.bicev.util;

import java.util.List;

import ru.bicev.dto.HealthCheckLogDto;
import ru.bicev.dto.MonitoredServiceDto;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.entity.MonitoredService;

public class Mapper {

    public static MonitoredService toServiceEntity(MonitoredServiceDto dto) {
        var entity = new MonitoredService();
        entity.name = dto.name();
        entity.url = dto.url();
        entity.checkIntervalSeconds = dto.checkIntervalSeconds();
        entity.expectedStatusCode = dto.expectedStatusCode();
        entity.active = dto.active();
        return entity;
    }

    public static MonitoredServiceDto toServiceDto(MonitoredService entity) {
        return new MonitoredServiceDto(
                entity.id,
                entity.name,
                entity.url,
                entity.checkIntervalSeconds,
                entity.expectedStatusCode,
                entity.active);
    }

    public static void updateServiceEntityFromDto(MonitoredService entity, MonitoredServiceDto dto) {
        entity.name = dto.name();
        entity.url = dto.url();
        entity.checkIntervalSeconds = dto.checkIntervalSeconds();
        entity.expectedStatusCode = dto.expectedStatusCode();
        entity.active = dto.active();
    }

    public static List<MonitoredServiceDto> toServiceDtoList(List<MonitoredService> entities) {
        return entities.stream()
                .map(Mapper::toServiceDto)
                .toList();
    }

    public static HealthCheckLogDto toHealthCheckLogDto(HealthCheckLog entity) {
        return new HealthCheckLogDto(
                entity.id,
                entity.service.id,
                entity.checkedAt,
                entity.statusCode,
                entity.responseTimeMs,
                entity.isSuccess,
                entity.failureReason);
    }

    public static List<HealthCheckLogDto> toHealthCheckLogDtoList(List<HealthCheckLog> entities) {
        return entities.stream()
                .map(Mapper::toHealthCheckLogDto)
                .toList();
    }

}
