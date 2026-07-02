package ru.bicev.util;

import java.util.List;

import ru.bicev.dto.HealthCheckLogDto;
import ru.bicev.dto.MonitoredServiceDto;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.entity.MonitoredService;

/**
 * Class responsible for mapping between DTOs and entity classes.
 */
public class Mapper {

    /**
     * Maps a MonitoredServiceDto to a MonitoredService entity.
     * 
     * @param dto The MonitoredServiceDto to be mapped
     * @return The corresponding MonitoredService entity
     */
    public static MonitoredService toServiceEntity(MonitoredServiceDto dto) {
        var entity = new MonitoredService();
        entity.name = dto.name();
        entity.url = dto.url();
        entity.checkIntervalSeconds = dto.checkIntervalSeconds();
        entity.expectedStatusCode = dto.expectedStatusCode();
        entity.active = dto.active();
        return entity;
    }

    /**
     * Maps a MonitoredService entity to a MonitoredServiceDto DTO.
     * 
     * @param entity The MonitoredService entity to be mapped
     * @return The corresponding MonitoredServiceDto DTO
     */
    public static MonitoredServiceDto toServiceDto(MonitoredService entity) {
        return new MonitoredServiceDto(
                entity.id,
                entity.name,
                entity.url,
                entity.checkIntervalSeconds,
                entity.expectedStatusCode,
                entity.active);
    }

    /**
     * Updates a MonitoredService entity with values from a MonitoredServiceDto.
     * 
     * @param entity The MonitoredService entity to be updated
     * @param dto    The MonitoredServiceDto containing the new values
     */
    public static void updateServiceEntityFromDto(MonitoredService entity, MonitoredServiceDto dto) {
        entity.name = dto.name();
        entity.url = dto.url();
        entity.checkIntervalSeconds = dto.checkIntervalSeconds();
        entity.expectedStatusCode = dto.expectedStatusCode();
        entity.active = dto.active();
    }

    /**
     * Maps a list of MonitoredService entities to a list of MonitoredServiceDto
     * DTOs
     * 
     * @param entities The list of MonitoredService entites to be mapped
     * @return The list of corresponding MonitoredServiceDto DTOs
     */
    public static List<MonitoredServiceDto> toServiceDtoList(List<MonitoredService> entities) {
        return entities.stream()
                .map(Mapper::toServiceDto)
                .toList();
    }

    /**
     * Maps a HealthCheckLog entity to a HealthCheckLogDto DTO
     * 
     * @param entity The HealthCheckLog entity to be mapped
     * @return The corresponding HealthCehckLogDto DTO
     */
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

    /**
     * Maps a list of HealthCheckLog entities to a HealthCheckLogDto DTOs
     * 
     * @param entities list of HealthCheckLog entities to be mapped
     * @return list of corresponding HealthCheckLogDto DTOs
     */
    public static List<HealthCheckLogDto> toHealthCheckLogDtoList(List<HealthCheckLog> entities) {
        return entities.stream()
                .map(Mapper::toHealthCheckLogDto)
                .toList();
    }

}
