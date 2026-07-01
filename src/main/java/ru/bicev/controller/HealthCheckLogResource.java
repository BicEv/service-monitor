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

@Path("/api/v1/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthCheckLogResource {

    @Inject
    private HealthCheckLogRepository logRepository;

    @GET
    @Path("/service/{serviceId}")
    public List<HealthCheckLogDto> getLogsByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findByServiceId(serviceId, pageNum, size));
    }

    @GET
    @Path("/failures")
    public List<HealthCheckLogDto> getAllFailures(
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findAllFailures(pageNum, size));
    }

    @GET
    @Path("/service/{serviceId}/failures")
    public List<HealthCheckLogDto> getFailuresByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findFailuresByService(serviceId, pageNum, size));
    }

    @GET
    @Path("/latest")
    public List<HealthCheckLogDto> getLatestLogs(
            @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findLastLogs(limit, pageNum, size));
    }

    @GET
    @Path("/status")
    public List<HealthCheckLogDto> getLogsByStatusCode(
            @QueryParam("statusCode") @DefaultValue("500") int statusCode,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(logRepository.findByStatusCode(statusCode, pageNum, size));
    }
}
