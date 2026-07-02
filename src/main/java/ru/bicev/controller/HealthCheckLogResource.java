package ru.bicev.controller;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import ru.bicev.dto.HealthCheckLogDto;
import ru.bicev.repo.HealthCheckLogRepository;
import ru.bicev.util.Mapper;

/**
 * Contoller that provides API for access health check logs.
 * Base URL of controller is "/api/v1/logs"
 */
@Path("/api/v1/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthCheckLogResource {

    /** Bean of HealhCheckLogRepository */
    @Inject
    private HealthCheckLogRepository logRepository;

    /**
     * Returns list of health check logs for the provided service ID, with pagination
     * @param serviceId     ID of the service 
     * @param pageNum       Number of page for pagination
     * @param size          Size of page for pagination
     * @return              List of JSON serialized HealthCheckLogDto DTOs for specified service
     */
    @GET
    @Path("/service/{serviceId}")
    public List<HealthCheckLogDto> getLogsByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findByServiceId(serviceId, pageNum, size));
    }

    /**
     * Returns list of health check logs with status failed, with pagination support
     * @param pageNum       Number of page for pagination   
     * @param size          Size of page for pagination
     * @return              List of JSON serialized HealthCheckLogDto DTOs which were failed
     */
    @GET
    @Path("/failures")
    public List<HealthCheckLogDto> getAllFailures(
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findAllFailures(pageNum, size));
    }

    /**
     * Returns list of health check logs for the provided service ID with failed status, with pagination
     * @param serviceId     ID of the service 
     * @param pageNum       Number of page for pagination
     * @param size          Size of page for pagination
     * @return              List of JSON serialized HealthCheckLogDto DTOs for specified service which were failed
     */
    @GET
    @Path("/service/{serviceId}/failures")
    public List<HealthCheckLogDto> getFailuresByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findFailuresByService(serviceId, pageNum, size));
    }

    /**
     * Returns list of latest health check logs, limited with the given paramether, with pagination
     * @param limit         Limit of the returned list size
     * @param pageNum       Number of page for pagination
     * @param size          Size of page for pagination
     * @return              List of latest JSON serialized HealthCheckLogDto DTOs limited with the given limit
     */
    @GET
    @Path("/latest")
    public List<HealthCheckLogDto> getLatestLogs(
            @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findLastLogs(limit, pageNum, size));
    }

    /**
     * Returns list of  health check logs, for the provided HTTP status code, with pagination 
     * @param statusCode    HTTP status code
     * @param pageNum       Number of page for pagination    
     * @param size          Size of page for pagination
     * @return              List of  JSON serialized HealthCheckLogDto DTOs with the given status code
     */
    @GET
    @Path("/status")
    public List<HealthCheckLogDto> getLogsByStatusCode(
            @QueryParam("statusCode") @DefaultValue("500") int statusCode,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findByStatusCode(statusCode, pageNum, size));
    }
}
